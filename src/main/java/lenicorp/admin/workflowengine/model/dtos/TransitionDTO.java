package lenicorp.admin.workflowengine.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class TransitionDTO
{
    private Long transitionId;
    private String privilegeCode;
    private String libelle;
    private String color;
    private String icon;
    private Integer ordre;
    private String statutOrigineCode;
    private String statutOrigineName;
    private String defaultStatutDestinationCode;
    private String defaultStatutDestinationName;
    private Long workflowId;
    private Boolean active = true;

    private boolean commentRequired = false;
    private List<String> requiredDocTypeCodes;
    private List<SideEffectDTO> sideEffects;

    public TransitionDTO(Long transitionId, String privilegeCode, String libelle, String color, String icon, Integer ordre, String statutOrigineCode, String statutOrigineName, String defaultStatutDestinationCode, String defaultStatutDestinationName, Long workflowId, Boolean active, boolean commentRequired) {
        this.transitionId = transitionId;
        this.privilegeCode = privilegeCode;
        this.libelle = libelle;
        this.color = color;
        this.icon = icon;
        this.ordre = ordre;
        this.statutOrigineCode = statutOrigineCode;
        this.statutOrigineName = statutOrigineName;
        this.defaultStatutDestinationCode = defaultStatutDestinationCode;
        this.defaultStatutDestinationName = defaultStatutDestinationName;
        this.workflowId = workflowId;
        this.active = active;
        this.commentRequired = commentRequired;
    }
}
