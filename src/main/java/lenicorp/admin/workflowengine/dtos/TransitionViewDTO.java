package lenicorp.admin.workflowengine.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class TransitionViewDTO {
    private String code;
    private String libelle;
    private String nextStatus;
    private Integer order;
    private String privilegeCode;
    private String explanation;
}
