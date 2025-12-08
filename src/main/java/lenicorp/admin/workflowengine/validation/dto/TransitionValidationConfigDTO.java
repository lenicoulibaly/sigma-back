package lenicorp.admin.workflowengine.validation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class TransitionValidationConfigDTO {
    private String transitionPrivilegeCode;
    private Boolean commentRequired;
    private List<String> requiredDocTypeCodes;
}
