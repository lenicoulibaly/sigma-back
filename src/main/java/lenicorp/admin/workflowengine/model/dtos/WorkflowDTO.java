package lenicorp.admin.workflowengine.model.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class WorkflowDTO
{
    private Long id;
    private String code;
    private String libelle;
    private String typeCode; // optional category
    private String targetTableNameCode; // required target type/table
    private Boolean active = true;

    public WorkflowDTO(Long id, String code, String libelle, String typeCode, String targetTableNameCode, Boolean active) {
        this.id = id;
        this.code = code;
        this.libelle = libelle;
        this.typeCode = typeCode;
        this.targetTableNameCode = targetTableNameCode;
        this.active = active;
    }

    // Definition des étapes du workflow
    private List<WorkflowStatusDTO> statuses;
}
