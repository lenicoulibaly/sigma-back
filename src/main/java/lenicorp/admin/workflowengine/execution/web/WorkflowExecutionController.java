package lenicorp.admin.workflowengine.execution.web;

import lenicorp.admin.workflowengine.model.dtos.ExecuteTransitionRequestDTO;
import lenicorp.admin.workflowengine.model.dtos.ExecuteTransitionResponseDTO;
import lenicorp.admin.workflowengine.model.dtos.TransitionDTO;
import lenicorp.admin.workflowengine.execution.service.WorkflowExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Generic controller to apply a workflow transition while capturing runtime data
 * like comment, attachments and contextual fields.
 */
@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
public class WorkflowExecutionController {
    private final WorkflowExecutionService workflowExecutionService;

    @GetMapping("/{workflowCode}/objects/{objectType}/{objectId}/available-transitions")
    public ResponseEntity<List<TransitionDTO>> getAvailableTransitions(
            @PathVariable String workflowCode,
            @PathVariable String objectType,
            @PathVariable String objectId
    ) {
        try {
            return ResponseEntity.ok(workflowExecutionService.getAvailableTransitions(workflowCode, objectType, objectId));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping(value = "/{workflowCode}/objects/{objectType}/{objectId}/transitions/{transitionId}",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExecuteTransitionResponseDTO> applyTransition(
            @PathVariable String workflowCode,
            @PathVariable String objectType,
            @PathVariable String objectId,
            @PathVariable Long transitionId,
            @RequestPart("request") ExecuteTransitionRequestDTO request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "fileTypes", required = false) List<String> fileTypes
    ) {
        try {
            ExecuteTransitionResponseDTO resp = workflowExecutionService.applyTransition(
                    workflowCode, objectType, objectId, transitionId, request, files, fileTypes
            );
            return ResponseEntity.ok(resp);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
