package lenicorp.admin.workflowengine.execution.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lenicorp.admin.security.controller.services.specs.IJwtService;
import lenicorp.admin.workflowengine.controller.repositories.TransitionRepository;
import lenicorp.admin.workflowengine.controller.repositories.TransitionRuleRepository;
import lenicorp.admin.workflowengine.controller.service.TransitionValidationService;
import lenicorp.admin.workflowengine.engine.adapter.ObjectAdapter;
import lenicorp.admin.workflowengine.engine.adapter.ObjectAdapterRegistry;
import lenicorp.admin.workflowengine.engine.rules.RuleEvaluationService;
import lenicorp.admin.workflowengine.execution.archive.ArchiveGateway;
import lenicorp.admin.workflowengine.execution.dto.AttachmentRef;
import lenicorp.admin.workflowengine.model.dtos.ExecuteTransitionRequestDTO;
import lenicorp.admin.workflowengine.model.dtos.ExecuteTransitionResponseDTO;
import lenicorp.admin.workflowengine.model.dtos.TransitionDTO;
import lenicorp.admin.workflowengine.model.entities.Transition;
import lenicorp.admin.workflowengine.outbox.model.payload.OutboxAction;
import lenicorp.admin.workflowengine.outbox.model.payload.TransitionAppliedPayload;
import lenicorp.admin.workflowengine.outbox.service.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowExecutionServiceImpl implements WorkflowExecutionService {
    private final ObjectAdapterRegistry adapterRegistry;
    private final ArchiveGateway archiveGateway;
    private final WorkflowTransitionLogService logService;
    private final TransitionValidationService validationService;
    private final TransitionRepository transitionRepo;
    private final TransitionRuleRepository ruleRepo;
    private final RuleEvaluationService ruleEngine;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;
    private final IJwtService jwtService;

    @Override
    @Transactional
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ExecuteTransitionResponseDTO applyTransition(
            String workflowCode,
            String objectType,
            String objectId,
            Long transitionId,
            ExecuteTransitionRequestDTO request,
            List<MultipartFile> files,
            List<String> fileTypes
    ) {
        Transition transition = transitionRepo.findById(transitionId)
                .orElseThrow(() -> new NoSuchElementException("Transition not found: " + transitionId));
        String transitionPrivilegeCode = transition.getPrivilege() != null ? transition.getPrivilege().getCode() : null;

        if (transitionPrivilegeCode != null && !jwtService.hasPrivilege(transitionPrivilegeCode)) {
            throw new IllegalStateException("Vous n'avez pas le privilège requis pour effectuer cette action : " + transitionPrivilegeCode);
        }

        ObjectAdapter adapter = adapterRegistry.adapterFor(objectType);
        Object aggregate = adapter.load(objectId);
        if (aggregate == null) {
            throw new NoSuchElementException(objectType + " not found: " + objectId);
        }

        // Validate data-driven config (comment + required doc types)
        Long objIdLong = tryParseLong(objectId);
        var vr = validationService.validate(transitionId, objectType, objIdLong,
                request != null ? request.getComment() : null,
                fileTypes);
        if (!vr.valid()) {
            throw new IllegalArgumentException("Validation failed for transition " + transitionId);
        }

        String from = adapter.getCurrentStatus(aggregate);

        // Evaluate Rules
        Map<String, Object> facts = adapter.toRuleMap(aggregate);
        var rules = ruleRepo.findActiveRulesByTransitionId(transitionId);
        String to = ruleEngine.evaluate(rules, facts);

        if (to == null && transition.getDefaultStatutDestination() != null) {
            to = transition.getDefaultStatutDestination().code;
        }

        if (to == null) {
            throw new IllegalStateException("Could not determine destination status for transition " + transitionId);
        }

        // Apply transition
        adapter.setStatus(aggregate, to);
        adapter.save(aggregate);

        // Persist attachments via archive module
        List<AttachmentRef> atts = archiveGateway.saveAll(files, fileTypes, objIdLong, objectType);

        // Log the transition
        Map<String, Object> ctx = request != null && request.getContext()!=null ? request.getContext() : Map.of();
        logService.logTransition(
                workflowCode,
                transitionId,
                transitionPrivilegeCode,
                objectType,
                objectId,
                from,
                to,
                request != null ? request.getComment() : null,
                ctx,
                atts
        );

        // Enqueue side effects in Outbox
        if (transition.getSideEffects() != null && !transition.getSideEffects().isEmpty()) {
            TransitionAppliedPayload payload = new TransitionAppliedPayload();
            payload.setWorkflowCode(workflowCode);
            payload.setTransitionCode(transition.getLibelle());
            payload.setObjectType(objectType);
            payload.setObjectId(objectId);
            payload.setFromStatus(from);
            payload.setToStatus(to);
            payload.setFacts(facts);

            List<OutboxAction> actions = transition.getSideEffects().stream()
                    .map(se -> {
                        OutboxAction action = new OutboxAction();
                        action.setName(se.getName());
                        action.setActionType(se.getActionType());
                        action.setDedupKey(workflowCode + ":" + objectType + ":" + objectId + ":" + transitionId + ":" + se.getId());
                        try {
                            if (se.getActionConfig() != null && !se.getActionConfig().isBlank()) {
                                action.setConfig(objectMapper.readValue(se.getActionConfig(), Map.class));
                            }
                        } catch (Exception e) {
                            // Invalid JSON config, skip or handle error
                        }
                        return action;
                    }).collect(Collectors.toList());

            payload.setActions(actions);
            outboxService.enqueueTransitionApplied(payload);
        }

        return new ExecuteTransitionResponseDTO(objectId, from, to, transitionId, transitionPrivilegeCode, null, null);
    }

    @Override
    public List<TransitionDTO> getAvailableTransitions(String workflowCode, String objectType, String objectId) {
        ObjectAdapter adapter = adapterRegistry.adapterFor(objectType);
        Object aggregate = adapter.load(objectId);
        if (aggregate == null) {
            throw new NoSuchElementException(objectType + " not found: " + objectId);
        }
        String currentStatus = adapter.getCurrentStatus(aggregate);
        if (currentStatus == null) return List.of();

        List<TransitionDTO> transitions = transitionRepo.findAvailableTransitions(workflowCode, currentStatus);
        return transitions.stream()
                .filter(t -> t.getPrivilegeCode() == null || jwtService.hasPrivilege(t.getPrivilegeCode()))
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAvailableObjectTypes() {
        return adapterRegistry.getAvailableTargetTypes();
    }

    private Long tryParseLong(String v) {
        try { return Long.valueOf(v); } catch (Exception e) { return null; }
    }
}
