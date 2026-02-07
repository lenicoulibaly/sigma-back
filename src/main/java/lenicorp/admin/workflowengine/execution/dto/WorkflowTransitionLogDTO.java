package lenicorp.admin.workflowengine.execution.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class WorkflowTransitionLogDTO {
    private Long id;
    private String workflowCode;
    private Long transitionId;
    private String transitionLibelle;
    private String transitionPrivilegeCode;
    private String objectType;
    private String objectId;
    private String fromStatus;
    private String toStatus;
    private String actorUsername;
    private String comment;
    private String contextJson;
    private LocalDateTime occurredAt;
    private List<AttachmentRef> attachments;
}
