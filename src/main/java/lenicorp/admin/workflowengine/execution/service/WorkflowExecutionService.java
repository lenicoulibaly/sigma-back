package lenicorp.admin.workflowengine.execution.service;

import lenicorp.admin.workflowengine.model.dtos.ExecuteTransitionRequestDTO;
import lenicorp.admin.workflowengine.model.dtos.ExecuteTransitionResponseDTO;
import lenicorp.admin.workflowengine.model.dtos.TransitionDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WorkflowExecutionService {
    ExecuteTransitionResponseDTO applyTransition(
            String workflowCode,
            String objectType,
            String objectId,
            Long transitionId,
            ExecuteTransitionRequestDTO request,
            List<MultipartFile> files,
            List<String> fileTypes
    );

    List<TransitionDTO> getAvailableTransitions(String workflowCode, String objectType, String objectId, boolean onlyVisible);

    List<String> getAvailableObjectTypes();
}
