package lenicorp.admin.workflowengine.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class TransitionValidationConfigDTO {
    private Long transitionId;
    private Boolean commentRequired;
    private List<String> requiredDocTypeCodes;
}
