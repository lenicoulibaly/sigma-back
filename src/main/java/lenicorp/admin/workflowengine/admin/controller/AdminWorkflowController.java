package lenicorp.admin.workflowengine.admin.controller;

import lenicorp.admin.workflowengine.admin.dto.WorkflowAdminDTO;
import lenicorp.admin.workflowengine.admin.service.AdminWorkflowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/workflows")
@RequiredArgsConstructor
public class AdminWorkflowController {
    private final AdminWorkflowService service;

    @GetMapping
    public List<WorkflowAdminDTO> listAll() {
        return service.listAll();
    }

    @GetMapping("/search")
    public Page<WorkflowAdminDTO> search(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return service.search(key, active, PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkflowAdminDTO> get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    public WorkflowAdminDTO create(@RequestBody WorkflowAdminDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkflowAdminDTO> update(@PathVariable Long id, @RequestBody WorkflowAdminDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
