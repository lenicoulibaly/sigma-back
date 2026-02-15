package lenicorp.admin.workflowengine.execution.service;

import lenicorp.admin.workflowengine.execution.dto.AttachmentRef;
import lenicorp.admin.workflowengine.execution.dto.WorkflowTransitionLogDTO;
import lenicorp.admin.workflowengine.execution.model.WorkflowTransitionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface WorkflowTransitionLogService {
    WorkflowTransitionLog logTransition(String workflowCode,
                       Long transitionId,
                       String transitionPrivilegeCode,
                       String objectType,
                       String objectId,
                       String fromStatus,
                       String toStatus,
                       String comment,
                       Map<String, Object> context,
                       List<AttachmentRef> attachments);

    Page<WorkflowTransitionLogDTO> getHistory(String objectType, String objectId, String key, List<Long> transitionIds, Pageable pageable);

    WorkflowTransitionLogDTO getLastLog(String objectType, String objectId);
}
