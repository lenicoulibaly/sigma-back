package lenicorp.admin.workflowengine.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoFieldDTO {
    private String label;
    private Object value;
    private String color;
}
