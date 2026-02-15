package lenicorp.metier.association.controller.services;

import lenicorp.admin.archive.controller.service.IDocumentService;
import lenicorp.admin.archive.model.dtos.request.UpdateDocReq;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.admin.exceptions.AppException;
import lenicorp.admin.security.controller.repositories.UserRepo;
import lenicorp.admin.security.model.entities.AppUser;
import lenicorp.admin.structures.model.entities.Structure;
import lenicorp.admin.utilities.StringUtils;
import lenicorp.admin.workflowengine.controller.repositories.WorkflowStatusGroupRepository;
import lenicorp.admin.workflowengine.controller.repositories.WorkflowStatusRepository;
import lenicorp.admin.workflowengine.engine.registry.WorkflowRegistry;
import lenicorp.admin.workflowengine.execution.service.WorkflowTransitionLogService;
import lenicorp.metier.association.controller.repositories.DemandeAdhesionRepository;
import lenicorp.metier.association.model.dtos.DemandeAdhesionDTO;
import lenicorp.metier.association.model.dtos.UserDemandeAdhesionDTO;
import lenicorp.metier.association.model.entities.DemandeAdhesion;
import lenicorp.metier.association.model.mappers.DemandeAdhesionMapper;
import lenicorp.admin.types.model.entities.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final UserRepo userRepo;
    private final IDocumentService documentService;

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

        // Upload des documents si présents
        if (dto.getDocuments() != null && !dto.getDocuments().isEmpty()) {
            for (UploadDocReq docReq : dto.getDocuments()) {
                docReq.setObjectId(entity.getDemandeId());
                docReq.setObjectTableName("DEMANDE_ADHESION");
                try {
                    documentService.uploadDocument(docReq);
                } catch (java.io.IOException e) {
                    throw new AppException("Erreur lors de l'upload d'un document : " + e.getMessage());
                }
            }
        }

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
    public DemandeAdhesionDTO createUserAndDemandeAdhesion(UserDemandeAdhesionDTO dto)
    {
        if(userRepo.existsByEmail(dto.getEmail())) throw new AppException("Email déjà attribué " + dto.getEmail());
        if(userRepo.existsByTel(dto.getTel())) throw new AppException("N° téléphone déjà attribué " + dto.getTel());
        if(userRepo.existsByMatricule(dto.getMatricule())) throw new AppException("Matricule déjà attribué " + dto.getMatricule());
        if(dto.getAssoId() == null) throw new AppException("Veuillez sélectionner l'association");

        // Create user
        AppUser user = new AppUser();
        user.setEmail(dto.getEmail());
        user.setMatricule(dto.getMatricule());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setTel(dto.getTel());
        user.setLieuNaissance(dto.getLieuNaissance());
        user.setDateNaissance(dto.getDateNaissance());
        user.setCodeCivilite(dto.getCodeCivilite());
        user.setGrade(dto.getGradeCode() == null ? null : new Type(dto.getGradeCode()));
        user.setIndice(dto.getIndice());
        user.setEmploi(dto.getEmploiCode() == null ? null : new Type(dto.getEmploiCode()));
        user.setStructure(dto.getStrId() == null ? null : new Structure(dto.getStrId()));
        user = userRepo.save(user);

        // Monter DemandeAdhesionDTO
        DemandeAdhesionDTO demandeDTO = new DemandeAdhesionDTO();
        demandeDTO.setAssoId(dto.getAssoId());
        demandeDTO.setSectionId(dto.getSectionId());
        demandeDTO.setDemandeurId(user.getUserId());
        demandeDTO.setAccepteCharte(dto.getAccepteCharte());
        demandeDTO.setAccepteRgpd(dto.getAccepteRgpd());
        demandeDTO.setAccepteStatutsReglements(dto.getAccepteStatutsReglements());
        demandeDTO.setMessage(dto.getMessage());
        demandeDTO.setDocuments(dto.getDocuments());

        return this.create(demandeDTO);
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

        // Mise à jour des documents
        if (dto.getDocuments() != null && !dto.getDocuments().isEmpty()) {
            for (UploadDocReq docReq : dto.getDocuments()) {
                try {
                    if (docReq.getDocId() == null)
                    {
                        // Nouveau document
                        docReq.setObjectId(entity.getDemandeId());
                        docReq.setObjectTableName("DEMANDE_ADHESION");
                        documentService.uploadDocument(docReq);
                    }
                    else if (docReq.getFile() != null)
                    {
                        // Mise à jour du document existant
                        UpdateDocReq updateDocReq = new UpdateDocReq(
                                docReq.getDocId(),
                                docReq.getDocTypeCode(),
                                docReq.getDocNum(),
                                docReq.getDocDescription(),
                                docReq.getFile()
                        );
                        documentService.updateDocument(updateDocReq);
                    }
                    // Si docId est présent mais file est null, on ne fait rien (pas de modification)
                } catch (java.io.IOException e) {
                    throw new AppException("Erreur lors de la mise à jour des documents : " + e.getMessage());
                }
            }
        }

        return mapper.toDto(entity);
    }

    @Override
    public Page<DemandeAdhesionDTO> search(Long associationId, Long userId, String key, String workflowStatusGroupCode, Pageable pageable) {
        String normalizedKey = key == null ? null : "%" + StringUtils.stripAccentsToUpperCase(key) + "%";
        List<String> statusCodes = null;

        if (workflowStatusGroupCode != null) {
            statusCodes = statusGroupRepository.findStatusCodesByGroupCode(workflowStatusGroupCode);
            
            if (statusCodes.isEmpty()) {
                // Si le groupe existe mais n'a pas de statuts, on renvoie une liste vide via un code inexistant
                statusCodes = List.of("__NONE__");
            }
        }

        return repository.search(associationId, userId, normalizedKey, statusCodes, pageable);
    }

    @Override
    public Page<DemandeAdhesionDTO> searchForUser(Long userId, String key, List<Long> assoIds, List<String> workflowStatusGroupCodes, Pageable pageable)
    {
        List<String> statusCodes = new ArrayList<>();
        boolean hasStatusFilter = false;

        if (workflowStatusGroupCodes != null && !workflowStatusGroupCodes.isEmpty()) {
            hasStatusFilter = true;
            for (String groupCode : workflowStatusGroupCodes) {
                statusCodes.addAll(statusGroupRepository.findStatusCodesByGroupCode(groupCode));
            }
            if (statusCodes.isEmpty()) {
                statusCodes.add("__NONE__");
            }
        }

        Page<DemandeAdhesionDTO> page = repository.searchForUser(userId, key, assoIds, statusCodes, hasStatusFilter, pageable);

        return page;
    }

    @Override
    public DemandeAdhesionDTO findById(Long id) {
        DemandeAdhesionDTO dto = repository.findByIdCustom(id)
                .orElseThrow(() -> new NoSuchElementException("Demande d'adhésion introuvable avec l'ID : " + id));
        dto.setDocuments(documentService.searchObjectDocs(id, "DEMANDE_ADHESION", null, Pageable.unpaged())
                .getContent().stream().map(doc -> {
                    UploadDocReq req = new UploadDocReq();
                    req.setObjectId(id);
                    req.setDocName(doc.getDocName());
                    req.setDocNum(doc.getDocNum());
                    req.setDocDescription(doc.getDocDescription());
                    req.setDocTypeCode(doc.getDocUniqueCode());
                    req.setObjectTableName("DEMANDE_ADHESION");
                    req.setDocId(doc.getDocId());
                    req.setDocMimeType(doc.getDocMimeType());
                    return req;
                }).toList());
        return dto;
    }

    private DemandeAdhesionDTO toDto(DemandeAdhesion d) {
        return mapper.toDto(d);
    }
}
