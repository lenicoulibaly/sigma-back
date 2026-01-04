package lenicorp.admin.workflowengine.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class WorkflowStatusDTO
{
    private Long id;
    private String statusCode;
    private Integer ordre;
    private Integer regulatoryDurationValue;
    private String regulatoryDurationUnitCode; // Type code for unit (e.g., HEURE, JOUR)
    private Boolean start;
    private Boolean end;
    private String statusName; // Libellé du type du status
    private String color;
    private String icon;
    private List<Long> groupIds;
    private List<String> groupCodes;
    private List<String> groupNames;

    public WorkflowStatusDTO(Long id, String statusCode, Integer ordre, Integer regulatoryDurationValue, String regulatoryDurationUnitCode, Boolean start, Boolean end, String statusName, String color, String icon) {
        this.id = id;
        this.statusCode = statusCode;
        this.ordre = ordre;
        this.regulatoryDurationValue = regulatoryDurationValue;
        this.regulatoryDurationUnitCode = regulatoryDurationUnitCode;
        this.start = start;
        this.end = end;
        this.statusName = statusName;
        this.color = color;
        this.icon = icon;
    }
}
