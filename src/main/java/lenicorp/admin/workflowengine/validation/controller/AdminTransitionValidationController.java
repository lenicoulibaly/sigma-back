package lenicorp.admin.workflowengine.validation.controller;

import lenicorp.admin.workflowengine.validation.dto.TransitionValidationConfigDTO;
import lenicorp.admin.workflowengine.validation.service.AdminTransitionValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/transition-validations")
@RequiredArgsConstructor
public class AdminTransitionValidationController {
    private final AdminTransitionValidationService service;

    @GetMapping("/{transitionPrivilegeCode}")
    public ResponseEntity<TransitionValidationConfigDTO> get(@PathVariable String transitionPrivilegeCode) {
        var dto = service.get(transitionPrivilegeCode);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PutMapping("/{transitionPrivilegeCode}")
    public ResponseEntity<TransitionValidationConfigDTO> upsert(@PathVariable String transitionPrivilegeCode,
                                                                @RequestBody TransitionValidationConfigDTO dto) {
        try {
            TransitionValidationConfigDTO res = service.upsert(transitionPrivilegeCode, dto);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{transitionPrivilegeCode}")
    public ResponseEntity<Void> delete(@PathVariable String transitionPrivilegeCode) {
        service.delete(transitionPrivilegeCode);
        return ResponseEntity.noContent().build();
    }
}
