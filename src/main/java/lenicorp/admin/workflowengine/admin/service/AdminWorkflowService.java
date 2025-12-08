package lenicorp.admin.workflowengine.admin.service;

import lenicorp.admin.workflowengine.admin.dto.WorkflowAdminDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdminWorkflowService {
    List<WorkflowAdminDTO> listAll();
    ResponseEntity<WorkflowAdminDTO> get(Long id);
    WorkflowAdminDTO create(WorkflowAdminDTO dto);
    ResponseEntity<WorkflowAdminDTO> update(Long id, WorkflowAdminDTO dto);
    ResponseEntity<Void> delete(Long id);
}
