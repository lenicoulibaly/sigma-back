package lenicorp.admin.workflowengine.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ExecuteTransitionResponseDTO {
    private String objectId;
    private String fromStatus;
    private String toStatus;
    private String transitionCode;
    private String ruleMatched; // optionnel: id/ordre de la règle gagnante
    private String auditId;     // si vous liez à un audit externe, sinon null
}
