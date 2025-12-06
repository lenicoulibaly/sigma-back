package lenicorp.admin.workflowengine.outbox.exec;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Minimal ${...} placeholder resolver over a flat context of maps (event, facts, headers, fetch, ctx).
 * Supports dotted paths like event.objectId and facts.montant. If not found, returns the original expression.
 */
public class ExpressionResolver {
    private static final Pattern P = Pattern.compile("\\$\\{([^}]+)}");

    public Object eval(Object expr, Map<String, Object> vars) {
        if (expr == null) return null;
        if (!(expr instanceof String s)) return expr; // raw value
        Matcher m = P.matcher(s);
        StringBuffer sb = new StringBuffer();
        boolean any = false;
        while (m.find()) {
            any = true;
            String path = m.group(1).trim();
            Object v = resolvePath(path, vars);
            m.appendReplacement(sb, Matcher.quoteReplacement(v == null ? "" : String.valueOf(v)));
        }
        m.appendTail(sb);
        return any ? sb.toString() : s;
    }

    @SuppressWarnings("unchecked")
    private Object resolvePath(String path, Map<String, Object> vars) {
        if (path == null || path.isBlank()) return null;
        String[] parts = path.split("\\.");
        Object cur = vars;
        for (String p : parts) {
            if (cur instanceof Map<?,?> map) {
                cur = map.get(p);
            } else {
                return null;
            }
        }
        return cur;
    }
}
