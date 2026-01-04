package lenicorp.admin.workflowengine.controller.service;

import lenicorp.admin.workflowengine.model.dtos.WorkflowDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdminWorkflowService {
    List<WorkflowDTO> listAll();
    Page<WorkflowDTO> search(String key, Boolean active, PageRequest pageRequest);
    ResponseEntity<WorkflowDTO> get(Long id);
    WorkflowDTO create(WorkflowDTO dto);
    ResponseEntity<WorkflowDTO> update(Long id, WorkflowDTO dto);
    ResponseEntity<Void> delete(Long id);
}
