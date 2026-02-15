package lenicorp.admin.workflowengine.controller.service;

import lenicorp.admin.workflowengine.model.dtos.TransitionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface AdminTransitionService {
    List<TransitionDTO> listAll();
    TransitionDTO get(Long transitionId);
    TransitionDTO create(TransitionDTO dto);
    TransitionDTO update(Long transitionId, TransitionDTO dto);
    void delete(Long transitionId);
    List<TransitionDTO> listByWorkflow(Long workflowId);
    Page<TransitionDTO> searchByWorkflow(Long workflowId, String key, List<String> originStatusCodes, List<String> destinationStatusCodes, Pageable pageable);
    void reorder(List<Map<String, Object>> items);
    String testNextStatus(Long transitionId, Map<String, Object> facts);
}
