package lenicorp.admin.workflowengine.controller.web;

import lenicorp.admin.workflowengine.execution.service.WorkflowExecutionService;
import lenicorp.admin.workflowengine.model.dtos.WorkflowDTO;
import lenicorp.admin.workflowengine.controller.service.AdminWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
public class WorkflowController
{
    private final AdminWorkflowService service;
    private final WorkflowExecutionService workflowExecutionService;

    @GetMapping
    public List<WorkflowDTO> listAll() {
        return service.listAll();
    }

    @GetMapping("/object-types")
    public ResponseEntity<List<String>> getAvailableObjectTypes() {
        return ResponseEntity.ok(workflowExecutionService.getAvailableObjectTypes());
    }

    @GetMapping("/search")
    public Page<WorkflowDTO> search(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return service.search(key, active, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowDTO> get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public WorkflowDTO create(@RequestBody WorkflowDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkflowDTO> update(@PathVariable Long id, @RequestBody WorkflowDTO dto)
    {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        return service.delete(id);
    }
}
