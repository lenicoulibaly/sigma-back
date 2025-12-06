package lenicorp.admin.workflowengine.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor
public class ExecuteTransitionRequestDTO {
    private String transitionCode;
    private String comment;
    private Map<String, Object> context;
    private String workflowCode; // optionnel quand plusieurs workflows existent pour le même objet
}
