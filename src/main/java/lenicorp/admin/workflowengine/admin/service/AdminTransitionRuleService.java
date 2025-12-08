package lenicorp.admin.workflowengine.admin.service;

import lenicorp.admin.workflowengine.admin.dto.TransitionRuleAdminDTO;

import java.util.List;
import java.util.Map;

public interface AdminTransitionRuleService {
    List<TransitionRuleAdminDTO> listAll();
    List<TransitionRuleAdminDTO> listByTransition(String privilegeCode);
    TransitionRuleAdminDTO get(Long id);
    TransitionRuleAdminDTO create(TransitionRuleAdminDTO dto);
    TransitionRuleAdminDTO update(Long id, TransitionRuleAdminDTO dto);
    void delete(Long id);

    boolean validateJson(String ruleJson);
    String test(String transitionPrivilegeCode, Map<String, Object> facts);
}
