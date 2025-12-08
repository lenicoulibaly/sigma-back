package lenicorp.admin.workflowengine.execution.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lenicorp.admin.security.controller.services.specs.IJwtService;
import lenicorp.admin.workflowengine.execution.dto.AttachmentRef;
import lenicorp.admin.workflowengine.execution.model.WorkflowTransitionAttachment;
import lenicorp.admin.workflowengine.execution.model.WorkflowTransitionLog;
import lenicorp.admin.workflowengine.execution.repo.WorkflowTransitionLogRepository;
import lenicorp.admin.workflowengine.execution.service.WorkflowTransitionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkflowTransitionLogServiceImpl implements WorkflowTransitionLogService {
    private final WorkflowTransitionLogRepository logRepository;
    private final IJwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void logTransition(String workflowCode, String transitionCode, String objectType, String objectId, String fromStatus, String toStatus, String comment, Map<String, Object> context, List<AttachmentRef> attachments) {
        WorkflowTransitionLog log = new WorkflowTransitionLog();
        log.setWorkflowCode(workflowCode);
        log.setTransitionCode(transitionCode);
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

    private String toJson(Map<String, Object> ctx) {
        if (ctx == null || ctx.isEmpty()) return null;
        try {
            return objectMapper.writeValueAsString(ctx);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
