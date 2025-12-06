package lenicorp.admin.workflowengine.outbox.exec;

import org.springframework.context.ApplicationContext;

import java.util.Map;

public record ActionContext(
        String dedupKey,
        Map<String, Object> event,
        Map<String, Object> facts,
        Map<String, Object> config,
        ExpressionResolver resolver,
        ApplicationContext applicationContext
) {}
