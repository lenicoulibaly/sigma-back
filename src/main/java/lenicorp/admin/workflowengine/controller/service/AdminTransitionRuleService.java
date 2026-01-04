package lenicorp.admin.workflowengine.controller.service;

import lenicorp.admin.workflowengine.model.dtos.TransitionRuleDTO;

import java.util.List;
import java.util.Map;

public interface AdminTransitionRuleService {
    List<TransitionRuleDTO> listAll();
    List<TransitionRuleDTO> listByTransition(Long transitionId);
    TransitionRuleDTO get(Long id);
    TransitionRuleDTO create(TransitionRuleDTO dto);
    TransitionRuleDTO update(Long id, TransitionRuleDTO dto);
    void delete(Long id);

    boolean validateJson(String ruleJson);
    String test(Long transitionId, Map<String, Object> facts);
}
