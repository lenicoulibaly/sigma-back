package lenicorp.admin.structures.model.dtos;

import lenicorp.admin.structures.model.validators.CompatibleTypeAndStrParent;
import lenicorp.admin.structures.model.validators.ExistingStrId;
import lenicorp.admin.structures.model.validators.UniqueSigleUnderSameParent;
import lenicorp.admin.structures.model.validators.UniqueStrNameUnderSameParent;
import lenicorp.admin.types.model.validators.ExistingStrTypeCode;
import lenicorp.admin.utilities.validatorgroups.CreateGroup;
import lenicorp.admin.utilities.validatorgroups.UpdateGroup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor //@Entity
@CompatibleTypeAndStrParent
@UniqueSigleUnderSameParent(groups = {CreateGroup.class, UpdateGroup.class})
@UniqueStrNameUnderSameParent(groups = {CreateGroup.class, UpdateGroup.class})
@NotNull(message = "Aucune donnée parvenue")
public class CreateOrUpdateStrDTO
{
    @NotNull(groups = {UpdateGroup.class}, message = "L'ID est obligatoire")
    @Null(groups = {CreateGroup.class}, message = "L'ID de la structure doit être nul")
    @ExistingStrId(groups = {UpdateGroup.class})
    private Long strId;
    @Length(message = "Le nom de la structure doit contenir au moins 3 caractères", min = 3)
    @NotNull(message = "Le nom de la structure ne peut être nul")
    private String strName;
    @NotNull(message = "Le sigle de la structure ne peut être nul")
    private String strSigle;
    @NotNull(message = "Le type de la structure ne peut être nul")
    @ExistingStrTypeCode
    private String typeCode;
    @ExistingStrId(allowNull = true)
    private Long parentId;

    private String strTel;
    private String strAddress;
    @NotNull(message = "La situation géographique ne peut être nulle")
    private String situationGeo;

    @Override
    public String toString()
    {
        return this.strName + " ("+this.strSigle + ")";
    }

}
