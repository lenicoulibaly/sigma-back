package lenicorp.admin.structures.model.dtos;

import lenicorp.admin.structures.model.validators.CompatibleTypeAndStrParent;
import lenicorp.admin.structures.model.validators.ExistingStrId;
import lenicorp.admin.structures.model.validators.UniqueSigleUnderSameParent;
import lenicorp.admin.structures.model.validators.UniqueStrNameUnderSameParent;
import lenicorp.admin.types.model.validators.ExistingTypeCode;
import lenicorp.admin.utilities.validatorgroups.CreateGroup;
import lenicorp.admin.utilities.validatorgroups.UpdateGroup;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@CompatibleTypeAndStrParent
@UniqueSigleUnderSameParent(groups = {CreateGroup.class, UpdateGroup.class})
@UniqueStrNameUnderSameParent(groups = {CreateGroup.class, UpdateGroup.class})
@NotNull(message = "Aucune donnée parvenue")
public class ChangeAnchorDTO
{
    @NotNull(message = "L'ID de la structure ne peut être nul")
    private Long strId;
    @ExistingTypeCode @NotNull(message = "L'ID du type ne peut être nul")
    private String typeCode;
    @ExistingStrId(allowNull = true)
    private Long parentId;

    @Length(message = "Le nom de la structure doit contenir au moins 3 caractères", min = 3)
    @NotNull(message = "Le nom de la structure ne peut être nul")
    private String strName;

    @NotNull(message = "Le sigle de la structure ne peut être nul")
    private String strSigle;

    private String strTel;
    private String strAddress;
    @NotNull(message = "La situation géographique ne peut être nulle")
    private String situationGeo;

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof ChangeAnchorDTO)) return false;
        ChangeAnchorDTO that = (ChangeAnchorDTO) o;
        return Objects.equals(strId, that.strId) && Objects.equals(typeCode, that.typeCode) && Objects.equals(parentId, that.parentId)
                && Objects.equals(strName, that.strName) && Objects.equals(strSigle, that.strSigle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(strId, typeCode, parentId, strName, strSigle);
    }
}
