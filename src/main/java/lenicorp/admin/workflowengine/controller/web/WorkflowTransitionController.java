package lenicorp.admin.workflowengine.controller.web;

import lenicorp.admin.workflowengine.model.dtos.TransitionDTO;
import lenicorp.admin.workflowengine.controller.service.AdminTransitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/transitions")
@RequiredArgsConstructor
public class WorkflowTransitionController
{
    private final AdminTransitionService service;

    @GetMapping
    public List<TransitionDTO> listAll() {
        return service.listAll();
    }

    @GetMapping("/{transitionId}")
    public ResponseEntity<TransitionDTO> get(@PathVariable Long transitionId) {
        TransitionDTO dto = service.get(transitionId);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<TransitionDTO> create(@RequestBody TransitionDTO dto) {
        try {
            return ResponseEntity.ok(service.create(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).build();
        }
    }

    @PutMapping("/{transitionId}")
    public ResponseEntity<TransitionDTO> update(@PathVariable Long transitionId, @RequestBody TransitionDTO dto) {
        try {
            TransitionDTO res = service.update(transitionId, dto);
            return ResponseEntity.ok(res);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{transitionId}")
    public ResponseEntity<Void> delete(@PathVariable Long transitionId) {
        service.delete(transitionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-workflow/{workflowId}")
    public List<TransitionDTO> listByWorkflow(@PathVariable Long workflowId) {
        return service.listByWorkflow(workflowId);
    }

    @GetMapping("/by-workflow/{workflowId}/search")
    public Page<TransitionDTO> searchTransitionsByWorkflow(
            @PathVariable Long workflowId,
            @RequestParam(value = "key", required = false, defaultValue = "") String key,
            @RequestParam(value = "originStatusCodes", required = false) List<String> originStatusCodes,
            @RequestParam(value = "destinationStatusCodes", required = false) List<String> destinationStatusCodes,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size)
    {
        return service.searchByWorkflow(workflowId, key, originStatusCodes, destinationStatusCodes, PageRequest.of(page, size));
    }

    @PostMapping("/reorder")
    public ResponseEntity<Void> reorder(@RequestBody List<Map<String, Object>> body) {
        if (body == null) return ResponseEntity.badRequest().build();
        service.reorder(body);
        return ResponseEntity.noContent().build();
    }

    public record TestRequest(Map<String, Object> facts) {}
    public record TestResponse(String nextStatus, String matchedByRule) {}

    @PostMapping("/{transitionId}/_test")
    public ResponseEntity<TestResponse> test(@PathVariable Long transitionId, @RequestBody TestRequest req) {
        String dest = service.testNextStatus(transitionId, req != null ? req.facts() : Map.of());
        return dest == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(new TestResponse(dest, null));
    }
}
