package lenicorp.metier.association.controller.services;

import lenicorp.admin.utilities.StringUtils;
import lenicorp.admin.workflowengine.controller.repositories.WorkflowStatusGroupRepository;
import lenicorp.admin.workflowengine.controller.repositories.WorkflowStatusRepository;
import lenicorp.admin.workflowengine.engine.registry.WorkflowRegistry;
import lenicorp.admin.workflowengine.execution.service.WorkflowTransitionLogService;
import lenicorp.metier.association.controller.repositories.DemandeAdhesionRepository;
import lenicorp.metier.association.model.dtos.DemandeAdhesionDTO;
import lenicorp.metier.association.model.entities.DemandeAdhesion;
import lenicorp.metier.association.model.mappers.DemandeAdhesionMapper;
import lenicorp.admin.types.model.entities.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DemandeAdhesionServiceImpl implements DemandeAdhesionService
{

    private final DemandeAdhesionRepository repository;
    private final WorkflowRegistry workflowRegistry;
    private final WorkflowStatusGroupRepository statusGroupRepository;
    private final WorkflowStatusRepository statusRepository;
    private final WorkflowTransitionLogService logService;
    private final DemandeAdhesionMapper mapper;

    @Override
    @Transactional
    public DemandeAdhesionDTO create(DemandeAdhesionDTO dto)
    {
        DemandeAdhesion entity = mapper.toEntity(dto);
        
        entity.setDateSoumission(LocalDateTime.now());

        // Initialisation du statut via le workflow engine
        String startStatusCode = statusRepository.findStartStatusCodeByWorkflowCode("DMD_ADH");
        if (startStatusCode == null) {
            throw new IllegalStateException("Le workflow DMD_ADH n'a pas de statut de départ défini.");
        }
        
        entity.setStatut(new Type(startStatusCode));

        entity = repository.save(entity);

        // Log de l'initialisation du workflow
        logService.logTransition(
                "DMD_ADH",
                null, // Pas de transition id pour une initialisation
                null,
                "DemandeAdhesion",
                entity.getDemandeId().toString(),
                null,
                startStatusCode,
                "Initialisation de la demande d'adhésion",
                null,
                null
        );

        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public DemandeAdhesionDTO update(Long id, DemandeAdhesionDTO dto)
    {
        DemandeAdhesion entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Demande d'adhésion introuvable avec l'ID : " + id));
        
        // Mise à jour des informations simples uniquement
        mapper.updateEntity(dto, entity);
        
        entity = repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    public Page<DemandeAdhesionDTO> search(String key, String workflowStatusGroupCode, Pageable pageable) {
        String normalizedKey = key == null ? null : "%" + StringUtils.stripAccentsToUpperCase(key) + "%";
        List<String> statusCodes = null;

        if (workflowStatusGroupCode != null) {
            statusCodes = statusGroupRepository.findStatusCodesByGroupCode(workflowStatusGroupCode);
            
            if (statusCodes.isEmpty()) {
                // Si le groupe existe mais n'a pas de statuts, on renvoie une liste vide via un code inexistant
                statusCodes = List.of("__NONE__");
            }
        }

        return repository.search(normalizedKey, statusCodes, pageable);
    }

    private DemandeAdhesionDTO toDto(DemandeAdhesion d) {
        return mapper.toDto(d);
    }
}
