package lenicorp.admin.security.model.dtos;

import lenicorp.admin.security.model.validators.ExistingAuthCode;
import lenicorp.admin.security.model.validators.UniqueAuthCode;
import lenicorp.admin.security.model.validators.UniqueAuthName;
import lenicorp.admin.types.model.validators.ExistingPrivilegeTypeCode;
import lenicorp.admin.types.model.validators.ExistingTypeCode;
import lenicorp.admin.utilities.validatorgroups.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link lenicorp.admin.security.model.entities.AppAuthority}
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@UniqueAuthName(groups = {UpdatPrvGroup.class, UpdateRolGroup.class, UpdatPrflGroup.class})
public class AuthorityDTO implements Serializable
{
    @NotNull(message = "Le code est obligatoire", groups = {CreateGroup.class, CreatePrvGroup.class})
    @NotBlank(message = "Le code est obligatoire", groups = {CreateGroup.class, CreatePrvGroup.class})
    @UniqueAuthCode(groups = {CreateGroup.class, CreatePrvGroup.class})
    String code;

    // Champs spécifiques pour les mises à jour
    @ExistingAuthCode(authType = "ROL", groups = {UpdateRolGroup.class})
    @NotNull(message = "Le code est obligatoire", groups = {UpdateRolGroup.class})
    @NotBlank(message = "Le code est obligatoire", groups = {UpdateRolGroup.class})
    String roleCode;

    @ExistingAuthCode(authType = "PRFL", groups = {UpdatPrflGroup.class})
    @NotNull(message = "Le code est obligatoire", groups = {UpdatPrflGroup.class})
    @NotBlank(message = "Le code est obligatoire", groups = {UpdatPrflGroup.class})
    String profileCode;

    @ExistingAuthCode(authType = "PRV", groups = {UpdatPrvGroup.class})
    @NotNull(message = "Le code est obligatoire", groups = {UpdatPrvGroup.class})
    @NotBlank(message = "Le code est obligatoire", groups = {UpdatPrvGroup.class})
    String privilegeCode;

    @UniqueAuthName(groups = {CreateGroup.class, CreatePrvGroup.class})
    String name;
    String description;
    @ExistingTypeCode(groups = {CreateGroup.class})
    @NotNull(message = "Le type est obligatoire")
    @NotBlank(message = "Le type est obligatoire")
    String typeCode;
    String typeName;
    @ExistingPrivilegeTypeCode(groups = {CreatePrvGroup.class, UpdatPrvGroup.class})
    @NotNull(message = "Le type de privilege est obligatoire", groups = {CreatePrvGroup.class, UpdatPrvGroup.class})
    @NotBlank(message = "Le type de privilege est obligatoire", groups = {CreatePrvGroup.class, UpdatPrvGroup.class})
    String privilegeTypeCode;
    String privilegeTypeName;
    List<AuthorityDTO> children;
    Long profileMaxAssignation;

    public AuthorityDTO(String code, String name, String description, String typeCode, String typeName, String privilegeTypeCode, String privilegeTypeName)
    {
        this.code = code;
        this.name = name;
        this.description = description;
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.privilegeTypeCode = privilegeTypeCode;
        this.privilegeTypeName = privilegeTypeName;
    }

    public AuthorityDTO(String code, String name, String description, String typeCode, String typeName, String privilegeTypeCode, String privilegeTypeName, String authType)
    {
        this.code = code;
        this.name = name;
        this.description = description;
        this.typeCode = typeCode;
        this.typeName = typeName;
        this.privilegeTypeCode = privilegeTypeCode;
        this.privilegeTypeName = privilegeTypeName;
        if(authType != null)
        {
            if(authType.equals("PRV")) this.privilegeCode = code;
            if(authType.equals("ROL")) this.roleCode = code;
            if(authType.equals("PRFL")) this.profileCode = code;
        }
    }

    public AuthorityDTO(String code, String name, String description, String typeCode, String typeName, String authType)
    {
        this.code = code;
        this.name = name;
        this.description = description;
        this.typeCode = typeCode;
        this.typeName = typeName;
        if(authType != null)
        {
            if(authType.equals("ROL")) this.roleCode = code;
            if(authType.equals("PRFL")) this.profileCode = code;
        }
    }
}