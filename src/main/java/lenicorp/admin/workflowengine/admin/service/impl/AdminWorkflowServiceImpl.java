package lenicorp.admin.workflowengine.admin.service.impl;

import lenicorp.admin.workflowengine.admin.dto.WorkflowAdminDTO;
import lenicorp.admin.workflowengine.admin.mapper.WorkflowAdminMapper;
import lenicorp.admin.workflowengine.admin.service.AdminWorkflowService;
import lenicorp.admin.workflowengine.model.entities.Workflow;
import lenicorp.admin.workflowengine.model.repositories.WorkflowRepository;
import lenicorp.admin.utilities.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminWorkflowServiceImpl implements AdminWorkflowService {

    private final WorkflowRepository workflowRepo;
    private final WorkflowAdminMapper mapper;

    @Override
    public List<WorkflowAdminDTO> listAll() {
        return workflowRepo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<WorkflowAdminDTO> search(String key, Boolean active, PageRequest pageRequest) {
        key = "%" + StringUtils.stripAccentsToUpperCase(key) + "%";
        Page<Workflow> page = workflowRepo.search(key, active, pageRequest);
        List<WorkflowAdminDTO> content = page.getContent().stream().map(mapper::toDto).collect(Collectors.toList());
        return new PageImpl<>(content, pageRequest, page.getTotalElements());
    }

    @Override
    public ResponseEntity<WorkflowAdminDTO> get(Long id) {
        return workflowRepo.findById(id)
                .map(w -> ResponseEntity.ok(mapper.toDto(w)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    @Transactional
    public WorkflowAdminDTO create(WorkflowAdminDTO dto) {
        Workflow entity = mapper.toEntity(dto);
        entity = workflowRepo.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public ResponseEntity<WorkflowAdminDTO> update(Long id, WorkflowAdminDTO dto) {
        return workflowRepo.findById(id).map(entity -> {
            dto.setId(id);
            mapper.updateEntity(dto, entity);
            Workflow saved = workflowRepo.save(entity);
            return ResponseEntity.ok(mapper.toDto(saved));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Override
    @Transactional
    public ResponseEntity<Void> delete(Long id) {
        if (!workflowRepo.existsById(id)) return ResponseEntity.notFound().build();
        workflowRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
