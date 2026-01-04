package lenicorp.admin.workflowengine.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class TransitionViewDTO {
    private Long transitionId;
    private String libelle;
    private String color;
    private String icon;
    private String nextStatus;
    private Integer order;
    private String privilegeCode;
    private String explanation;
}
