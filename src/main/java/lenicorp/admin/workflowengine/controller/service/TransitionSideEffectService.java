package lenicorp.admin.workflowengine.controller.service;

import lenicorp.admin.workflowengine.model.dtos.TransitionSideEffectDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TransitionSideEffectService {
    TransitionSideEffectDTO create(TransitionSideEffectDTO dto);
    TransitionSideEffectDTO update(Long id, TransitionSideEffectDTO dto);
    void delete(Long id);
    Page<TransitionSideEffectDTO> search(Long transitionId, String key, Pageable pageable);
    List<TransitionSideEffectDTO> findByTransitionId(Long transitionId);
}
