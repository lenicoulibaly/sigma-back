package lenicorp.admin.workflowengine.controller.service;

import lenicorp.admin.workflowengine.model.dtos.WorkflowStatusDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface WorkflowStatusService
{

    List<WorkflowStatusDTO> listByWorkflow(Long workflowId);

    Page<WorkflowStatusDTO> search(Long workflowId, String key, Pageable pageable);

    WorkflowStatusDTO create(Long workflowId, WorkflowStatusDTO dto);

    ResponseEntity<WorkflowStatusDTO> update(Long id, WorkflowStatusDTO dto);
}
