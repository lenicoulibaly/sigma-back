package lenicorp.admin.workflowengine.execution.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lenicorp.admin.security.controller.services.specs.IJwtService;
import lenicorp.admin.workflowengine.controller.repositories.TransitionRepository;
import lenicorp.admin.workflowengine.execution.dto.AttachmentRef;
import lenicorp.admin.workflowengine.execution.dto.WorkflowTransitionLogDTO;
import lenicorp.admin.workflowengine.execution.model.WorkflowTransitionAttachment;
import lenicorp.admin.workflowengine.execution.model.WorkflowTransitionLog;
import lenicorp.admin.workflowengine.execution.repo.WorkflowTransitionLogRepository;
import lenicorp.admin.workflowengine.model.entities.Transition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkflowTransitionLogServiceImpl implements WorkflowTransitionLogService {
    private final WorkflowTransitionLogRepository logRepository;
    private final TransitionRepository transitionRepository;
    private final IJwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void logTransition(String workflowCode, Long transitionId, String transitionPrivilegeCode, String objectType, String objectId, String fromStatus, String toStatus, String comment, Map<String, Object> context, List<AttachmentRef> attachments)
    {
        WorkflowTransitionLog log = new WorkflowTransitionLog();
        log.setWorkflowCode(workflowCode);
        log.setTransitionId(transitionId);
        log.setTransitionPrivilegeCode(transitionPrivilegeCode);
        log.setObjectType(objectType);
        log.setObjectId(objectId);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        var user = jwtService.getCurrentUser();
        log.setActorUsername(user != null ? user.getEmail() : null);
        log.setComment(comment);
        log.setContextJson(toJson(context));

        if (attachments != null && !attachments.isEmpty()) {
            List<WorkflowTransitionAttachment> attEntities = new ArrayList<>();
            for (AttachmentRef a : attachments) {
                WorkflowTransitionAttachment att = new WorkflowTransitionAttachment();
                att.setLog(log);
                att.setDocumentId(a.getDocumentId());
                att.setName(a.getName());
                att.setContentType(a.getContentType());
                att.setSize(a.getSize());
                attEntities.add(att);
            }
            log.setAttachments(attEntities);
        }

        logRepository.save(log);
    }

    @Override
    public Page<WorkflowTransitionLogDTO> getHistory(String objectType, String objectId, String key, List<Long> transitionIds, Pageable pageable) {
        List<Long> finalTransitionIds = (transitionIds == null || transitionIds.isEmpty()) ? null : transitionIds;
        Page<WorkflowTransitionLog> page = logRepository.searchHistory(objectType, objectId, key, finalTransitionIds, pageable);
        return page.map(this::toDto);
    }

    private WorkflowTransitionLogDTO toDto(WorkflowTransitionLog log) {
        WorkflowTransitionLogDTO dto = new WorkflowTransitionLogDTO();
        dto.setId(log.getId());
        dto.setWorkflowCode(log.getWorkflowCode());
        dto.setTransitionId(log.getTransitionId());
        if (log.getTransitionId() != null)
        {
            String transitionLibelle = transitionRepository.getTransitionLibelleByid(log.getTransitionId());
            dto.setTransitionLibelle(transitionLibelle);
        }
        dto.setTransitionPrivilegeCode(log.getTransitionPrivilegeCode());
        dto.setObjectType(log.getObjectType());
        dto.setObjectId(log.getObjectId());
        dto.setFromStatus(log.getFromStatus());
        dto.setToStatus(log.getToStatus());
        dto.setActorUsername(log.getActorUsername());
        dto.setComment(log.getComment());
        dto.setContextJson(log.getContextJson());
        dto.setOccurredAt(log.getCreatedAt());
        if (log.getAttachments() != null) {
            dto.setAttachments(log.getAttachments().stream()
                    .map(a -> new AttachmentRef(a.getDocumentId(), a.getName(), a.getContentType(), a.getSize()))
                    .toList());
        }
        return dto;
    }

    private String toJson(Map<String, Object> ctx) {
        if (ctx == null || ctx.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(ctx);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
