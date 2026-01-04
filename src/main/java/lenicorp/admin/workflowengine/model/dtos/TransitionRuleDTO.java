package lenicorp.admin.workflowengine.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class TransitionRuleDTO
{
    private Long id;
    private Integer ordre;
    private Long transitionId;
    private String transitionPrivilegeCode; // link to Transition
    private String statutDestinationCode;   // Type code
    private String ruleJson;                // JSON condition
    private Boolean active = true;
}
