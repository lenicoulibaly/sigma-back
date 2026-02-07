package lenicorp.admin.workflowengine.model.dtos;

import lenicorp.admin.workflowengine.model.validators.UniqueWorkflowStatusGroupCode;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
@UniqueWorkflowStatusGroupCode(allowNull = true)
public class WorkflowStatusGroupDTO {
    private Long id;
    @NotBlank(message = "Le code est obligatoire")
    private String code;
    @NotBlank(message = "Le nom est obligatoire")
    private String name;
    private String description;
    private String color;
    private Integer ordre;
    private List<Long> statusIds;
    private List<String> statusCodes;
    private List<String> statusNames;
    private List<String> authorityCodes;
    private List<String> authorityNames;

    public WorkflowStatusGroupDTO(Long id, String code, String name, String description, String color, Integer ordre) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.color = color;
        this.ordre = ordre;
    }
}
