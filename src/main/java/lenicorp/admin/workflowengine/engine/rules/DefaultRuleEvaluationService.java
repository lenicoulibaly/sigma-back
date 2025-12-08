package lenicorp.admin.workflowengine.engine.rules;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lenicorp.admin.workflowengine.model.entities.TransitionRule;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class DefaultRuleEvaluationService implements RuleEvaluationService {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String evaluate(List<TransitionRule> rules, Map<String, Object> facts) {
        if (rules == null || rules.isEmpty()) return null;
        for (TransitionRule r : rules) {
            if (r.getActive() != null && !r.getActive()) continue;
            if (r.getRuleJson() == null || r.getRuleJson().isBlank()) continue;
            try {
                JsonNode root = mapper.readTree(r.getRuleJson());
                JsonNode cond = root.has("condition") ? root.get("condition") : root;
                if (matches(cond, facts)) {
                    return r.getStatutDestination() != null ? r.getStatutDestination().code : null;
                }
            } catch (Exception e) {
                // invalid rule json -> ignore and continue
            }
        }
        return null;
    }

    private boolean matches(JsonNode cond, Map<String, Object> facts) {
        if (cond == null || cond.isNull()) return false;
        boolean hasAny = cond.has("any");
        boolean hasAll = cond.has("all");
        boolean hasNone = cond.has("none");
        boolean result = true;

        if (hasAll) {
            result = all(cond.get("all"), facts);
        }
        if (hasAny) {
            boolean any = any(cond.get("any"), facts);
            result = hasAll ? (result && any) : any;
        }
        if (hasNone) {
            boolean none = none(cond.get("none"), facts);
            result = result && none;
        }
        // if none of all/any/none provided, treat node as a single predicate
        if (!hasAll && !hasAny && !hasNone) {
            return testPredicate(cond, facts);
        }
        return result;
    }

    private boolean all(JsonNode arr, Map<String, Object> facts) {
        if (arr == null || !arr.isArray()) return false;
        Iterator<JsonNode> it = arr.elements();
        while (it.hasNext()) {
            if (!testPredicate(it.next(), facts)) return false;
        }
        return true;
    }

    private boolean any(JsonNode arr, Map<String, Object> facts) {
        if (arr == null || !arr.isArray()) return false;
        Iterator<JsonNode> it = arr.elements();
        while (it.hasNext()) {
            if (testPredicate(it.next(), facts)) return true;
        }
        return false;
    }

    private boolean none(JsonNode arr, Map<String, Object> facts) {
        if (arr == null || !arr.isArray()) return true;
        Iterator<JsonNode> it = arr.elements();
        while (it.hasNext()) {
            if (testPredicate(it.next(), facts)) return false;
        }
        return true;
    }

    private boolean testPredicate(JsonNode node, Map<String, Object> facts) {
        if (node == null || !node.has("field")) return false;
        String field = node.get("field").asText();
        String operator = node.has("operator") ? node.get("operator").asText() : "==";
        JsonNode valueNode = node.get("value");
        Object left = facts.get(field);
        Object right = valueNode == null || valueNode.isNull() ? null : jsonToJava(valueNode);
        return compare(left, operator, right);
    }

    private Object jsonToJava(JsonNode n) {
        if (n == null || n.isNull()) return null;
        if (n.isNumber()) return n.numberValue();
        if (n.isBoolean()) return n.booleanValue();
        if (n.isTextual()) return n.textValue();
        if (n.isArray()) return mapper.convertValue(n, List.class);
        return mapper.convertValue(n, Map.class);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean compare(Object left, String op, Object right)
    {
        if(op == null  || op.isBlank() || op.equals("=")) op = "==";
        switch (op) {
            case "==": return Objects.equals(left, right);
            case "!=": return !Objects.equals(left, right);
            case ">": return cmp(left, right) > 0;
            case ">=": return cmp(left, right) >= 0;
            case "<": return cmp(left, right) < 0;
            case "<=": return cmp(left, right) <= 0;
            case "IN":
                if (right instanceof List l) return l.contains(left);
                return false;
            case "CONTAINS":
                if (left instanceof List l2) return l2.contains(right);
                if (left instanceof String s && right != null) return s.contains(String.valueOf(right));
                return false;
            case "EXISTS":
                return left != null;
            default:
                return false;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private int cmp(Object a, Object b) {
        if (a == null || b == null) return -1;
        if (a instanceof Number && b instanceof Number) {
            double da = ((Number) a).doubleValue();
            double db = ((Number) b).doubleValue();
            return Double.compare(da, db);
        }
        if (a instanceof Comparable && a.getClass().isInstance(b)) {
            return ((Comparable) a).compareTo(b);
        }
        // try string compare
        return String.valueOf(a).compareTo(String.valueOf(b));
    }
}
