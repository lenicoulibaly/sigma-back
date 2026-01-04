package lenicorp.admin.workflowengine.controller.service;

import lenicorp.admin.workflowengine.model.dtos.WorkflowStatusGroupDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface WorkflowStatusGroupService {
    WorkflowStatusGroupDTO create(WorkflowStatusGroupDTO dto);
    WorkflowStatusGroupDTO update(Long id, WorkflowStatusGroupDTO dto);
    void delete(Long id);
    Page<WorkflowStatusGroupDTO> search(String key, Pageable pageable);
    List<String> getAuthorityCodes(Long id);
}
