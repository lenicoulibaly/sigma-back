package lenicorp.admin.workflowengine.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class WorkflowAdminDTO {
    private Long id;
    private String code;
    private String libelle;
    private String typeCode; // optional category
    private String targetTableNameCode; // required target type/table
    private Boolean active = true;
}
