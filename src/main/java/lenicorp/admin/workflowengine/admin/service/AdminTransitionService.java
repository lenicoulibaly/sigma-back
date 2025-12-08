package lenicorp.admin.workflowengine.admin.service;

import lenicorp.admin.workflowengine.admin.dto.TransitionAdminDTO;

import java.util.List;
import java.util.Map;

public interface AdminTransitionService {
    List<TransitionAdminDTO> listAll();
    TransitionAdminDTO get(String privilegeCode);
    TransitionAdminDTO create(TransitionAdminDTO dto);
    TransitionAdminDTO update(String privilegeCode, TransitionAdminDTO dto);
    void delete(String privilegeCode);
    List<TransitionAdminDTO> listByWorkflow(Long workflowId);
    void reorder(List<Map<String, Object>> items);
    String testNextStatus(String privilegeCode, Map<String, Object> facts);
}
