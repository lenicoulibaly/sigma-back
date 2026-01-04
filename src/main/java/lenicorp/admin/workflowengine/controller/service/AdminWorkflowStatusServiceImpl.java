package lenicorp.admin.workflowengine.controller.service;

import lenicorp.admin.utilities.StringUtils;
import lenicorp.admin.workflowengine.model.dtos.WorkflowStatusDTO;
import lenicorp.admin.workflowengine.model.dtos.mapper.WorkflowStatusMapper;
import lenicorp.admin.workflowengine.model.entities.Workflow;
import lenicorp.admin.workflowengine.model.entities.WorkflowStatus;
import lenicorp.admin.workflowengine.controller.repositories.WorkflowRepository;
import lenicorp.admin.workflowengine.controller.repositories.WorkflowStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminWorkflowStatusServiceImpl implements WorkflowStatusService
{

    private final WorkflowStatusRepository statusRepo;
    private final WorkflowRepository workflowRepo;
    private final WorkflowStatusMapper mapper;
    private final lenicorp.admin.workflowengine.controller.repositories.WorkflowStatusGroupRepository groupRepo;

    @Override
    public List<WorkflowStatusDTO> listByWorkflow(Long workflowId) {
        return statusRepo.findByWorkflowId(workflowId).stream()
                .peek(dto -> {
                    WorkflowStatus entity = statusRepo.findById(dto.getId()).orElse(null);
                    if (entity != null) {
                        dto.setGroupIds(mapper.groupIdsFromGroups(entity.getGroups()));
                        dto.setGroupCodes(mapper.groupCodesFromGroups(entity.getGroups()));
                        dto.setGroupNames(mapper.groupNamesFromGroups(entity.getGroups()));
                    }
                }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Page<WorkflowStatusDTO> search(Long workflowId, String key, Pageable pageable) {
        String normalizedKey = "%" + StringUtils.stripAccentsToUpperCase(key) + "%";
        Page<WorkflowStatusDTO> page = statusRepo.searchByWorkflow(workflowId, normalizedKey, pageable);
        page.getContent().forEach(dto -> {
            WorkflowStatus entity = statusRepo.findById(dto.getId()).orElse(null);
            if (entity != null) {
                dto.setGroupIds(mapper.groupIdsFromGroups(entity.getGroups()));
                dto.setGroupCodes(mapper.groupCodesFromGroups(entity.getGroups()));
                dto.setGroupNames(mapper.groupNamesFromGroups(entity.getGroups()));
            }
        });
        return page;
    }

    @Override
    @Transactional
    public WorkflowStatusDTO create(Long workflowId, WorkflowStatusDTO dto) {
        Workflow wf = workflowRepo.findById(workflowId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow introuvable: " + workflowId));

        // validations simples
        if (dto.getOrdre() == null) throw new IllegalArgumentException("Ordre obligatoire");
        if (dto.getStatusCode() == null || dto.getStatusCode().isBlank())
            throw new IllegalArgumentException("statusCode obligatoire");
        if (dto.getRegulatoryDurationValue() != null && dto.getRegulatoryDurationValue() < 0)
            throw new IllegalArgumentException("regulatoryDurationValue ne peut pas être négatif");

        if (Boolean.TRUE.equals(dto.getStart()) && statusRepo.existsByWorkflowIdAndStartTrue(workflowId))
            throw new IllegalArgumentException("Une étape start=true existe déjà pour ce workflow");
        if (Boolean.TRUE.equals(dto.getEnd()) && statusRepo.existsByWorkflowIdAndEndTrue(workflowId))
            throw new IllegalArgumentException("Une étape end=true existe déjà pour ce workflow");

        WorkflowStatus entity = mapper.toEntity(dto);
        if (entity.getStart() == null) entity.setStart(Boolean.FALSE);
        if (entity.getEnd() == null) entity.setEnd(Boolean.FALSE);
        entity.setWorkflow(wf);
        if (dto.getGroupIds() != null && !dto.getGroupIds().isEmpty()) {
            entity.setGroups(groupRepo.findAllById(dto.getGroupIds()));
        }

        entity = statusRepo.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public ResponseEntity<WorkflowStatusDTO> update(Long id, WorkflowStatusDTO dto) {
        return statusRepo.findById(id).map(entity -> {
            // si on change les flags start/end, revérifier unicité
            Long workflowId = entity.getWorkflow().getId();
            if (Boolean.TRUE.equals(dto.getStart()) && Boolean.FALSE.equals(entity.getStart())
                    && statusRepo.existsByWorkflowIdAndStartTrue(workflowId))
                throw new IllegalArgumentException("Une étape start=true existe déjà pour ce workflow");
            if (Boolean.TRUE.equals(dto.getEnd()) && Boolean.FALSE.equals(entity.getEnd())
                    && statusRepo.existsByWorkflowIdAndEndTrue(workflowId))
                throw new IllegalArgumentException("Une étape end=true existe déjà pour ce workflow");

            // mise à jour champ à champ depuis DTO
            if (dto.getStatusCode() != null) {
                entity.setStatus(mapper.typeFromCode(dto.getStatusCode()));
            }
            if (dto.getRegulatoryDurationUnitCode() != null) {
                entity.setRegulatoryDurationUnit(mapper.typeFromCode(dto.getRegulatoryDurationUnitCode()));
            }
            if (dto.getOrdre() != null) entity.setOrdre(dto.getOrdre());
            entity.setRegulatoryDurationValue(dto.getRegulatoryDurationValue());
            if (dto.getStart() != null) entity.setStart(dto.getStart());
            if (dto.getEnd() != null) entity.setEnd(dto.getEnd());
            entity.setColor(dto.getColor());
            entity.setIcon(dto.getIcon());
            if (dto.getGroupIds() != null) {
                entity.setGroups(groupRepo.findAllById(dto.getGroupIds()));
            } else {
                entity.setGroups(null);
            }

            WorkflowStatus saved = statusRepo.save(entity);
            return ResponseEntity.ok(mapper.toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }
}
