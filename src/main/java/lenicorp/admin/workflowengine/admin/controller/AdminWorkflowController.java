package lenicorp.admin.workflowengine.admin.controller;

import lenicorp.admin.workflowengine.admin.dto.WorkflowAdminDTO;
import lenicorp.admin.workflowengine.admin.service.AdminWorkflowService;
import lombok.RequiredArgsConstructor;
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
