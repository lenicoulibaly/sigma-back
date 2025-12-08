package lenicorp.admin.workflowengine.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class TransitionRuleAdminDTO {
    private Long id;
    private Integer ordre;
    private String transitionPrivilegeCode; // link to Transition
    private String statutDestinationCode;   // Type code
    private String ruleJson;                // JSON condition
    private Boolean active = true;
}
