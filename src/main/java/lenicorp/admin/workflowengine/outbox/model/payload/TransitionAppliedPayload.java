package lenicorp.admin.workflowengine.outbox.model.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransitionAppliedPayload {
    private String workflowCode;
    private String transitionCode;
    private String objectType;
    private String objectId;
    private String fromStatus;
    private String toStatus;
    private Map<String, Object> facts;
    private List<OutboxAction> actions; // actions to execute (RUN_BEAN_METHOD, SEND_EMAIL, ...)

    // optional metadata
    private String correlationId;
    private LocalDateTime occurredAt;
}
