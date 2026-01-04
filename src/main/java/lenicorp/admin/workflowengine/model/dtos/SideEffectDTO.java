package lenicorp.admin.workflowengine.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class SideEffectDTO {
    private Long id;
    private String name;
    private String actionType;
    private String actionConfig;
    private Integer ordre;
}
