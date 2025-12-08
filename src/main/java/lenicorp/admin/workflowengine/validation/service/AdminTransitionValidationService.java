package lenicorp.admin.workflowengine.validation.service;

import lenicorp.admin.workflowengine.validation.dto.TransitionValidationConfigDTO;

public interface AdminTransitionValidationService {
    TransitionValidationConfigDTO get(String transitionPrivilegeCode);
    TransitionValidationConfigDTO upsert(String transitionPrivilegeCode, TransitionValidationConfigDTO dto);
    void delete(String transitionPrivilegeCode);
}
