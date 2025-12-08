package lenicorp.admin.workflowengine.execution.service;

import lenicorp.admin.workflowengine.execution.dto.AttachmentRef;

import java.util.List;
import java.util.Map;

public interface WorkflowTransitionLogService {
    void logTransition(String workflowCode,
                       String transitionCode,
                       String objectType,
                       String objectId,
                       String fromStatus,
                       String toStatus,
                       String comment,
                       Map<String, Object> context,
                       List<AttachmentRef> attachments);
}
