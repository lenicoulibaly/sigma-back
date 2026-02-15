package lenicorp.admin.workflowengine.outbox.exec;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Expression resolver using Spring Expression Language (SpEL).
 * Supports ${...} placeholders and evaluates them using a context of variables.
 */
@Slf4j
public class ExpressionResolver {
    private static final Pattern P = Pattern.compile("\\$\\{([^}]+)}");
    private final ExpressionParser parser = new SpelExpressionParser();

    public Object eval(Object expr, Map<String, Object> vars) {
        if (expr == null) return null;
        if (!(expr instanceof String s)) return expr; // raw value
        
        Matcher m = P.matcher(s);
        boolean hasPlaceholder = false;
        
        // Optimize for single placeholder that covers the whole string
        if (s.startsWith("${") && s.endsWith("}") && s.indexOf("${", 2) == -1) {
            String expression = s.substring(2, s.length() - 1).trim();
            Object val = evaluateSpel(expression, vars);
            if (val == null) {
                // Try backward compatibility for dotted paths if SpEL failed or returned null
                val = resolveDottedPath(expression, vars);
            }
            return val;
        }

        StringBuilder sb = new StringBuilder();
        int lastEnd = 0;
        while (m.find()) {
            hasPlaceholder = true;
            sb.append(s, lastEnd, m.start());
            String expression = m.group(1).trim();
            Object result = evaluateSpel(expression, vars);
            if (result == null) {
                result = resolveDottedPath(expression, vars);
            }
            sb.append(result == null ? "" : result);
            lastEnd = m.end();
        }
        
        if (!hasPlaceholder) return s;
        
        sb.append(s.substring(lastEnd));
        return sb.toString();
    }

    private Object evaluateSpel(String expression, Map<String, Object> vars) {
        try {
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.addPropertyAccessor(new MapAccessor());
            // Ajouter chaque variable individuellement pour l'accès via #variable
            vars.forEach(context::setVariable);
            // Définir la map comme objet racine pour l'accès direct via MapAccessor
            context.setRootObject(vars);
            
            Expression exp = parser.parseExpression(expression);
            Object value = exp.getValue(context);
            if (value != null) {
                log.debug("[DEBUG_LOG] SpEL resolved [{}] to [{}]", expression, value);
            }
            return value;
        } catch (Exception e) {
            log.warn("[DEBUG_LOG] SpEL failed for expression [{}]: {}", expression, e.getMessage());
            return null;
        }
    }

    private Object resolveDottedPath(String path, Map<String, Object> vars) {
        if (path == null || path.isBlank()) return null;
        log.debug("[DEBUG_LOG] resolveDottedPath for [{}]", path);
        String[] parts = path.split("\\.");
        Object cur = vars;
        for (String p : parts) {
            if (cur instanceof Map<?, ?> map) {
                cur = map.get(p);
            } else {
                log.debug("[DEBUG_LOG] resolveDottedPath failed at part [{}] because cur is not a Map", p);
                return null;
            }
        }
        log.debug("[DEBUG_LOG] resolveDottedPath for [{}] result: [{}]", path, cur);
        return cur;
    }
}
