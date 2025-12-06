package lenicorp.admin.workflowengine.engine.rules;

import lenicorp.admin.workflowengine.model.entities.TransitionRule;

import java.util.List;
import java.util.Map;

public interface RuleEvaluationService {
    /**
     * Returns the destination status code for the first matching rule, or null if none matched.
     */
    String evaluate(List<TransitionRule> rules, Map<String, Object> facts);
}
