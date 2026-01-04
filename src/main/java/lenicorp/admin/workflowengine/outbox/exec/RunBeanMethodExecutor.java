package lenicorp.admin.workflowengine.outbox.exec;

import lenicorp.admin.workflowengine.outbox.service.DedupService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RunBeanMethodExecutor implements OutboxActionExecutor {
    public static final String TYPE = "RUN_BEAN_METHOD";

    private final DedupService dedupService;

    @Override
    public String actionType() {
        return TYPE;
    }

    @Override
    public void execute(ActionContext ctx) throws RuntimeException {
        dedupService.runOnce(ctx.dedupKey(), ctx.name(), () -> {
            Map<String, Object> cfg = ctx.config();
            String beanName = (String) cfg.get("beanName");
            String methodName = (String) cfg.get("method");
            List<?> argsExpr = (List<?>) cfg.getOrDefault("args", List.of());

            ApplicationContext app = ctx.applicationContext();
            Object bean = app.getBean(beanName);

            Object[] args = new Object[argsExpr.size()];
            for (int i = 0; i < argsExpr.size(); i++) {
                Object raw = argsExpr.get(i);
                args[i] = ctx.resolver().eval(raw, Map.of(
                        "event", ctx.event(),
                        "facts", ctx.facts(),
                        "config", cfg
                ));
            }

            invoke(bean, methodName, args);
        });
    }

    private void invoke(Object bean, String methodName, Object[] args) {
        Method m = resolveMethod(bean.getClass(), methodName, args.length);
        try {
            m.invoke(bean, args);
        } catch (ReflectiveOperationException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            if (cause instanceof RuntimeException re) throw re;
            throw new RuntimeException(cause);
        }
    }

    private Method resolveMethod(Class<?> type, String name, int arity) {
        for (Method m : type.getMethods()) {
            if (m.getName().equals(name) && m.getParameterCount() == arity) {
                return m;
            }
        }
        throw new IllegalArgumentException("No public method '" + name + "' with arity " + arity + " on bean " + type.getName());
    }
}
