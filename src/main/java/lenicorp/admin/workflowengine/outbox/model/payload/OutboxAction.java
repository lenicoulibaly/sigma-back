package lenicorp.admin.workflowengine.outbox.model.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OutboxAction {
    private String name;
    private String actionType;   // RUN_BEAN_METHOD | SEND_EMAIL
    private String dedupKey;     // idempotence key
    private Map<String, Object> config; // executor-specific configuration
}
