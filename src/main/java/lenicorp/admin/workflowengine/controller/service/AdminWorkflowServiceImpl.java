package lenicorp.admin.workflowengine.controller.service;

import lenicorp.admin.workflowengine.model.dtos.WorkflowDTO;
import lenicorp.admin.workflowengine.model.dtos.WorkflowStatusDTO;
import lenicorp.admin.workflowengine.model.dtos.mapper.WorkflowMapper;
import lenicorp.admin.workflowengine.model.dtos.mapper.WorkflowStatusMapper;
import lenicorp.admin.workflowengine.model.entities.Workflow;
import lenicorp.admin.workflowengine.model.entities.WorkflowStatus;
import lenicorp.admin.workflowengine.controller.repositories.WorkflowRepository;
import lenicorp.admin.workflowengine.controller.repositories.WorkflowStatusRepository;
import lenicorp.admin.utilities.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminWorkflowServiceImpl implements AdminWorkflowService
{
    private final WorkflowRepository workflowRepo;
    private final WorkflowMapper mapper;
    private final WorkflowStatusMapper statusMapper;
    private final WorkflowStatusRepository workflowStatusRepository;

    @Override
    public List<WorkflowDTO> listAll()
    {
        return workflowRepo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<WorkflowDTO> search(String key, Boolean active, PageRequest pageRequest)
    {
        key = "%" + StringUtils.stripAccentsToUpperCase(key) + "%";
        Page<WorkflowDTO> page = workflowRepo.search(key, active, pageRequest);
        page.getContent().forEach(dto -> {
            if (dto.getId() != null)
            {
                List<WorkflowStatusDTO> statuses = workflowStatusRepository
                        .findByWorkflowId(dto.getId());
                dto.setStatuses(statuses);
            }
        });
        return page;
    }

    @Override
    public ResponseEntity<WorkflowDTO> get(Long id)
    {
        return workflowRepo.findById(id)
                .map(w -> ResponseEntity.ok(mapper.toDto(w)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override @Transactional
    public WorkflowDTO create(WorkflowDTO dto)
    {
        Workflow entity = mapper.toEntity(dto);
        // persist first to have an attached entity
        entity = workflowRepo.save(entity);

        // Handle statuses if provided
        if (dto.getStatuses() != null && !dto.getStatuses().isEmpty())
        {
            validateStatuses(dto.getStatuses());
            final Workflow wf = entity;
            List<WorkflowStatus> steps = dto.getStatuses().stream()
                    .map(statusMapper::toEntity)
                    .peek(ws ->
                    {
                        // default flags
                        if (ws.getStart() == null) ws.setStart(Boolean.FALSE);
                        if (ws.getEnd() == null) ws.setEnd(Boolean.FALSE);
                        ws.setWorkflow(wf);
                    })
                    .sorted(Comparator.comparing(WorkflowStatus::getOrdre))
                    .collect(Collectors.toList());
            entity.getStatuses().clear();
            entity.getStatuses().addAll(steps);
            entity = workflowRepo.save(entity);
        }
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public ResponseEntity<WorkflowDTO> update(Long id, WorkflowDTO dto)
    {
        return workflowRepo.findById(id).map(entity ->
        {
            dto.setId(id);
            mapper.updateEntity(dto, entity);
            // Replace statuses if provided
            if (dto.getStatuses() != null)
            {
                validateStatuses(dto.getStatuses());
                final Workflow wf = entity;
                List<WorkflowStatus> steps = dto.getStatuses().stream()
                        .map(statusMapper::toEntity)
                        .peek(ws ->
                        {
                            if (ws.getStart() == null) ws.setStart(Boolean.FALSE);
                            if (ws.getEnd() == null) ws.setEnd(Boolean.FALSE);
                            ws.setWorkflow(wf);
                        })
                        .sorted(Comparator.comparing(WorkflowStatus::getOrdre))
                        .collect(Collectors.toList());
                entity.getStatuses().clear();
                entity.getStatuses().addAll(steps);
            }
            Workflow saved = workflowRepo.save(entity);
            return ResponseEntity.ok(mapper.toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Override
    @Transactional
    public ResponseEntity<Void> delete(Long id)
    {
        if (!workflowRepo.existsById(id)) return ResponseEntity.notFound().build();
        workflowRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    // region validations
    private void validateStatuses(List<WorkflowStatusDTO> statuses)
    {
        HashSet<Integer> ordres = new HashSet<>();
        HashSet<String> statusCodes = new HashSet<>();
        int startCount = 0;
        int endCount = 0;
        for (WorkflowStatusDTO s : statuses)
        {
            if (s.getOrdre() == null) throw new IllegalArgumentException("Ordre obligatoire pour chaque étape");
            if (s.getStatusCode() == null || s.getStatusCode().isBlank())
                throw new IllegalArgumentException("statusCode obligatoire pour chaque étape");
            if (s.getRegulatoryDurationValue() != null && s.getRegulatoryDurationValue() < 0)
                throw new IllegalArgumentException("regulatoryDurationValue ne peut pas être négatif");
            if (!ordres.add(s.getOrdre()))
                throw new IllegalArgumentException("Doublon sur l'ordre des étapes: " + s.getOrdre());
            if (!statusCodes.add(s.getStatusCode()))
                throw new IllegalArgumentException("Doublon sur statusCode: " + s.getStatusCode());
            if (Boolean.TRUE.equals(s.getStart())) startCount++;
            if (Boolean.TRUE.equals(s.getEnd())) endCount++;
        }
        if (startCount > 1) throw new IllegalArgumentException("Au plus une étape start=true");
        if (endCount > 1) throw new IllegalArgumentException("Au plus une étape end=true");
    }
}