package lenicorp.admin.workflowengine.controller.web;

import lenicorp.admin.workflowengine.model.dtos.TransitionRuleDTO;
import lenicorp.admin.workflowengine.controller.service.AdminTransitionRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transition-rules")
@RequiredArgsConstructor
public class AdminTransitionRuleController {
    private final AdminTransitionRuleService service;

    @GetMapping
    public List<TransitionRuleAdminDTO> listAll() {
        return service.listAll();
    }

    @GetMapping("/by-transition/{transitionId}")
    public List<TransitionRuleDTO> listByTransition(@PathVariable Long transitionId) {
        return service.listByTransition(transitionId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransitionRuleDTO> get(@PathVariable Long id) {
        var dto = service.get(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<TransitionRuleAdminDTO> create(@RequestBody TransitionRuleAdminDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransitionRuleAdminDTO> update(@PathVariable Long id, @RequestBody TransitionRuleAdminDTO dto) {
        var res = service.update(id, dto);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record ValidateRequest(String ruleJson) {}
    public record ValidateResponse(boolean valid, String errorMessage) {}

    @PostMapping("/_validate")
    public ResponseEntity<ValidateResponse> validate(@RequestBody ValidateRequest req) {
        boolean ok = service.validateJson(req.ruleJson());
        return ResponseEntity.ok(new ValidateResponse(ok, ok ? null : "Invalid JSON"));
    }

    public record TestRequest(String transitionPrivilegeCode, Map<String, Object> facts) {}
    public record TestResponse(String nextStatus) {}

    @PostMapping("/_test")
    public ResponseEntity<TestResponse> test(@RequestBody TestRequest req) {
        if (req.transitionPrivilegeCode() == null) return ResponseEntity.badRequest().build();
        String dest = service.test(req.transitionPrivilegeCode(), req.facts() != null ? req.facts() : Map.of());
        return ResponseEntity.ok(new TestResponse(dest));
    }
}
