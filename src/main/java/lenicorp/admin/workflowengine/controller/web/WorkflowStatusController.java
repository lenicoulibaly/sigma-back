package lenicorp.admin.workflowengine.controller.web;

import lenicorp.admin.workflowengine.model.dtos.WorkflowStatusDTO;
import lenicorp.admin.workflowengine.controller.service.WorkflowStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workflows/{workflowId}/statuses")
@RequiredArgsConstructor
public class WorkflowStatusController {

    private final WorkflowStatusService service;

    @GetMapping
    public List<WorkflowStatusDTO> list(@PathVariable Long workflowId) {
        return service.listByWorkflow(workflowId);
    }

    @GetMapping("/search")
    public Page<WorkflowStatusDTO> search(
            @PathVariable Long workflowId,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return service.search(workflowId, key, PageRequest.of(page, size));
    }

    @PostMapping
    public WorkflowStatusDTO create(@PathVariable Long workflowId, @RequestBody WorkflowStatusDTO dto) {
        return service.create(workflowId, dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkflowStatusDTO> update(@PathVariable Long workflowId, @PathVariable Long id,
                                                    @RequestBody WorkflowStatusDTO dto) {
        // workflowId is present in the path for consistency/authorization, but update is by status id.
        return service.update(id, dto);
    }
}
