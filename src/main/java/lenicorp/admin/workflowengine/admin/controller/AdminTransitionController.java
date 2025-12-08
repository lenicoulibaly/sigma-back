package lenicorp.admin.workflowengine.admin.controller;

import lenicorp.admin.workflowengine.admin.dto.TransitionAdminDTO;
import lenicorp.admin.workflowengine.admin.service.AdminTransitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin/transitions")
@RequiredArgsConstructor
public class AdminTransitionController {
    private final AdminTransitionService service;

    @GetMapping
    public List<TransitionAdminDTO> listAll() {
        return service.listAll();
    }

    @GetMapping("/{privilegeCode}")
    public ResponseEntity<TransitionAdminDTO> get(@PathVariable String privilegeCode) {
        TransitionAdminDTO dto = service.get(privilegeCode);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<TransitionAdminDTO> create(@RequestBody TransitionAdminDTO dto) {
        try {
            return ResponseEntity.ok(service.create(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PutMapping("/{privilegeCode}")
    public ResponseEntity<TransitionAdminDTO> update(@PathVariable String privilegeCode, @RequestBody TransitionAdminDTO dto) {
        try {
            TransitionAdminDTO res = service.update(privilegeCode, dto);
            return ResponseEntity.ok(res);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{privilegeCode}")
    public ResponseEntity<Void> delete(@PathVariable String privilegeCode) {
        service.delete(privilegeCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-workflow/{workflowId}")
    public List<TransitionAdminDTO> listByWorkflow(@PathVariable Long workflowId) {
        return service.listByWorkflow(workflowId);
    }

    @PostMapping("/reorder")
    public ResponseEntity<Void> reorder(@RequestBody List<Map<String, Object>> body) {
        if (body == null) return ResponseEntity.badRequest().build();
        service.reorder(body);
        return ResponseEntity.noContent().build();
    }

    public record TestRequest(Map<String, Object> facts) {}
    public record TestResponse(String nextStatus, String matchedByRule) {}

    @PostMapping("/{privilegeCode}/_test")
    public ResponseEntity<TestResponse> test(@PathVariable String privilegeCode, @RequestBody TestRequest req) {
        String dest = service.testNextStatus(privilegeCode, req != null ? req.facts() : Map.of());
        return dest == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(new TestResponse(dest, null));
    }
}
