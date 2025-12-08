package lenicorp.admin.workflowengine.validation.service.impl;

import lenicorp.admin.archive.controller.service.DocumentService;
import lenicorp.admin.workflowengine.validation.model.TransitionValidationConfig;
import lenicorp.admin.workflowengine.validation.repo.TransitionValidationConfigRepository;
import lenicorp.admin.workflowengine.validation.service.TransitionValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TransitionValidationServiceImpl implements TransitionValidationService {
    private final TransitionValidationConfigRepository cfgRepo;
    private final DocumentService documentService;

    @Override
    @Transactional(readOnly = true)
    public Result validate(String transitionPrivilegeCode, String objectTypeCode, Long objectId, String comment, List<String> uploadedDocTypeCodes) {
        TransitionValidationConfig cfg = cfgRepo.findById(transitionPrivilegeCode).orElse(null);
        if (cfg == null) return new Result(true, List.of()); // no rules -> valid

        List<Violation> violations = new ArrayList<>();

        // Comment required
        if (cfg.isCommentRequired()) {
            if (comment == null || comment.isBlank()) {
                violations.add(new Violation("workflow.validation.comment.required", Map.of()));
            }
        }

        // Required document types
        if (cfg.getRequiredDocTypes() != null && !cfg.getRequiredDocTypes().isEmpty()) {
            Set<String> present = new HashSet<>();
            // Already in DB for this object
            for (var t : cfg.getRequiredDocTypes()) {
                boolean exists = !documentService.getFileByObjectIdAndTableNameAndTypeCode(objectId, objectTypeCode, t.code).isEmpty();
                if (exists) present.add(t.code);
            }
            // Uploaded with current request
            if (uploadedDocTypeCodes != null) present.addAll(uploadedDocTypeCodes.stream().filter(Objects::nonNull).map(String::toUpperCase).toList());

            List<String> required = cfg.getRequiredDocTypes().stream().map(x -> x.code).toList();
            List<String> missing = required.stream().filter(r -> !present.contains(r)).toList();
            if (!missing.isEmpty()) {
                violations.add(new Violation("workflow.validation.docs.missing", Map.of("required", required, "missing", missing)));
            }
        }

        return new Result(violations.isEmpty(), violations);
    }
}
