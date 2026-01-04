package lenicorp.admin.workflowengine.controller.service;

import lenicorp.admin.workflowengine.model.dtos.TransitionValidationConfigDTO;

public interface AdminTransitionValidationService {
    TransitionValidationConfigDTO get(Long transitionId);
    TransitionValidationConfigDTO upsert(Long transitionId, TransitionValidationConfigDTO dto);
    void delete(Long transitionId);
}
