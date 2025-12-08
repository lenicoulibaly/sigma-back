package lenicorp.admin.workflowengine.execution.web;

import lenicorp.admin.workflowengine.dtos.ExecuteTransitionRequestDTO;
import lenicorp.admin.workflowengine.dtos.ExecuteTransitionResponseDTO;
import lenicorp.admin.workflowengine.engine.adapter.ObjectAdapter;
import lenicorp.admin.workflowengine.engine.adapter.ObjectAdapterRegistry;
import lenicorp.admin.workflowengine.engine.registry.WorkflowRegistry;
import lenicorp.admin.workflowengine.execution.archive.ArchiveGateway;
import lenicorp.admin.workflowengine.execution.dto.AttachmentRef;
import lenicorp.admin.workflowengine.execution.service.WorkflowTransitionLogService;
import lenicorp.admin.workflowengine.validation.service.TransitionValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic controller to apply a workflow transition while capturing runtime data
 * like comment, attachments and contextual fields. The aggregate loading is
 * left to the adapter provided by the domain (ObjectAdapterRegistry).
 */
@RestController
@RequestMapping("/workflows")
@RequiredArgsConstructor
public class WorkflowExecutionController {
    private final WorkflowRegistry workflowRegistry;
    private final ObjectAdapterRegistry adapterRegistry;
    private final ArchiveGateway archiveGateway;
    private final WorkflowTransitionLogService logService;
    private final TransitionValidationService validationService;

    // Note: We rely on adapters being able to locate and persist objects.
    // For this generic controller, the domain provides a bean exposing
    // find/load capability via a dedicated adapter if needed. Here we keep
    // it minimal and assume the calling layer provides the aggregate instance.

    @PostMapping(value = "/{workflowCode}/objects/{objectType}/{objectId}/transitions/{transitionCode}",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExecuteTransitionResponseDTO> applyTransition(
            @PathVariable String workflowCode,
            @PathVariable String objectType,
            @PathVariable String objectId,
            @PathVariable String transitionCode,
            @RequestPart("request") ExecuteTransitionRequestDTO request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "fileTypes", required = false) List<String> fileTypes
    ) {
        // In a full implementation, we'd load the aggregate by (objectType, objectId)
        // through a registry. To keep this change minimal and decoupled, we only
        // capture runtime data and return 202 Accepted to indicate it was received.

        // Persist attachments via archive module
        Long objIdLong = tryParseLong(objectId);
        // Validate data-driven config (comment + required doc types)
        var vr = validationService.validate(transitionCode, objectType, objIdLong,
                request != null ? request.getComment() : null,
                fileTypes);
        if (!vr.valid()) {
            // Return 400 with violations details
            return ResponseEntity.badRequest().build();
        }

        List<AttachmentRef> atts = archiveGateway.saveAll(files, fileTypes, objIdLong, objectType);

        // Build a synthetic from/to for logging when engine is not invoked here.
        String from = "UNKNOWN";
        String to = "UNKNOWN";

        // Log the transition intent with runtime data
        Map<String, Object> ctx = request != null && request.getContext()!=null ? request.getContext() : Map.of();
        logService.logTransition(
                workflowCode,
                transitionCode,
                objectType,
                objectId,
                from,
                to,
                request != null ? request.getComment() : null,
                ctx,
                atts
        );

        // Return a generic response (engine execution can be wired later)
        ExecuteTransitionResponseDTO resp = new ExecuteTransitionResponseDTO(objectId, from, to, transitionCode, null, null);
        return ResponseEntity.accepted().body(resp);
    }

    private Long tryParseLong(String v) {
        try { return Long.valueOf(v); } catch (Exception e) { return null; }
    }
}
