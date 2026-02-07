package lenicorp.admin.workflowengine.controller.web;

import lenicorp.admin.workflowengine.model.dtos.TransitionValidationConfigDTO;
import lenicorp.admin.workflowengine.controller.service.AdminTransitionValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transition-validations")
@RequiredArgsConstructor
public class AdminTransitionValidationController {
    private final AdminTransitionValidationService service;

    @GetMapping("/{transitionId}")
    public ResponseEntity<TransitionValidationConfigDTO> get(@PathVariable Long transitionId) {
        var dto = service.get(transitionId);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PutMapping("/{transitionId}")
    public ResponseEntity<TransitionValidationConfigDTO> upsert(@PathVariable Long transitionId,
                                                                @RequestBody TransitionValidationConfigDTO dto) {
        try {
            TransitionValidationConfigDTO res = service.upsert(transitionId, dto);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{transitionId}")
    public ResponseEntity<Void> delete(@PathVariable Long transitionId) {
        service.delete(transitionId);
        return ResponseEntity.noContent().build();
    }
}
