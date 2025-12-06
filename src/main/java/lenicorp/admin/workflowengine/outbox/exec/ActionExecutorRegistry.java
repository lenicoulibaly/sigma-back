package lenicorp.admin.workflowengine.outbox.exec;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ActionExecutorRegistry {
    private final Map<String, OutboxActionExecutor> byType = new HashMap<>();

    public ActionExecutorRegistry(List<OutboxActionExecutor> executors) {
        if (executors != null) {
            for (OutboxActionExecutor e : executors) {
                byType.put(e.actionType(), e);
            }
        }
    }

    public OutboxActionExecutor get(String actionType) {
        return byType.get(actionType);
    }
}
