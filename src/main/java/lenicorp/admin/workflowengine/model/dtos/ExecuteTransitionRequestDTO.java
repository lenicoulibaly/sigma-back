package lenicorp.admin.workflowengine.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor
public class ExecuteTransitionRequestDTO {
    private Long transitionId;
    private String transitionPrivilegeCode;
    private String comment;
    private Map<String, Object> context;
    private String workflowCode; // optionnel quand plusieurs workflows existent pour le même objet
}
