package lenicorp.admin.workflowengine.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class TransitionAdminDTO {
    private String privilegeCode; // id
    private String code;
    private String libelle;
    private Integer ordre;
    private String statutOrigineCode;
    private String defaultStatutDestinationCode;
    private Long workflowId;
    private Boolean active = true;
}
