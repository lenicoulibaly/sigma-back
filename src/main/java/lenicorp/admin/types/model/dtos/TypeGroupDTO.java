package lenicorp.admin.types.model.dtos;

import lenicorp.admin.types.model.validators.ExistingGroupCode;
import lenicorp.admin.types.model.validators.UniqueGroupCode;
import lenicorp.admin.utilities.validatorgroups.CreateGroup;
import lenicorp.admin.utilities.validatorgroups.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TypeGroupDTO
{
    @NotNull(message = "Le code du groupe ne peut être null")
    @NotBlank(message = "Le code du groupe ne peut être null")
    @UniqueGroupCode(groups = {CreateGroup.class})
    @ExistingGroupCode(groups = {UpdateGroup.class})
    private String groupCode;
    @NotNull(message = "Le nom du groupe ne peut être null")
    @NotBlank(message = "Le nom du groupe ne peut être null")
    private String name;

    public TypeGroupDTO(String groupCode) {
        this.groupCode = groupCode;
    }
}
