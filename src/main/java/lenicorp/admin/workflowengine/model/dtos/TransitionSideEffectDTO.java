package lenicorp.admin.workflowengine.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lenicorp.admin.utilities.UniqueSideEffectName;
import lenicorp.admin.utilities.ValidJson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@UniqueSideEffectName
public class TransitionSideEffectDTO {
    private Long id;
    
    @NotNull(message = "La transition est obligatoire")
    private Long transitionId;

    @NotBlank(message = "Le nom est obligatoire")
    private String name;
    
    @NotBlank(message = "Le type d'action est obligatoire")
    private String actionType;
    
    @ValidJson(message = "La configuration de l'action doit être au format JSON valide")
    private String actionConfig; // JSON
    
    private Integer ordre = 0;

    public TransitionSideEffectDTO(Long id, Long transitionId, String name, String actionType, String actionConfig, Integer ordre) {
        this.id = id;
        this.transitionId = transitionId;
        this.name = name;
        this.actionType = actionType;
        this.actionConfig = actionConfig;
        this.ordre = ordre;
    }
}
