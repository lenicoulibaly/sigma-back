package lenicorp.admin.security.model.dtos;

import lenicorp.admin.security.model.validators.*;
import lenicorp.admin.structures.model.validators.ExistingStrId;
import lenicorp.admin.types.model.validators.ExistingGradeCode;
import lenicorp.admin.types.model.validators.ExistingTypeCode;
import lenicorp.admin.utilities.validatorgroups.CreateGroup;
import lenicorp.admin.utilities.validatorgroups.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link lenicorp.admin.security.model.entities.AuthAssociation}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@NotNull
@UniqueUserProfileAssociation(groups = {CreateGroup.class, UpdateGroup.class})
@ProfileMaxAssignation(groups = {CreateGroup.class, UpdateGroup.class})
@DateConsistencyValidator(groups = {CreateGroup.class, UpdateGroup.class})
@EndingDateRequiredValidator(groups = {CreateGroup.class, UpdateGroup.class})
@UniqueMatricule(groups = {UpdateGroup.class})
public class UserProfileAssoDTO implements Serializable
{
    @ExistingAuthAssoId(message = "L'association n'existe pas ou n'est pas de type USR_PRFL", groups = {UpdateGroup.class})
    Long id;
    private String libelle;
    @ExistingUserId
    @NotNull(message = "L'utilisateur est obligatoire")
    Long userId;
    String email;
    @UniqueMatricule(groups = {CreateGroup.class})
    String matricule;
    @ExistingGradeCode(message = "Grade inconnu", groups = {CreateGroup.class, UpdateGroup.class}, allowNull = true)
    String gradeCode;
    @ExistingAuthCode(authType = "PRFL")
    @NotNull(message = "Le profil est obligatoire")
    @NotBlank(message = "Le profil est obligatoire")
    String profileCode;
    String profileName;
    @ExistingStrId
    @NotNull(message = "La structure est obligatoire")
    Long strId;
    String strName;
    @ExistingTypeCode(message = "Type d'assignation inconnu", groups = {CreateGroup.class, UpdateGroup.class}, typeGroupCode = "USR_PRFL_TYPE")
    String userProfileAssTypeCode;
    String userProfileAssTypeName;
    LocalDate startingDate;
    LocalDate endingDate;
    String assStatusCode;
    String assStatusName;
    Long ordre;
    String firstName;
    String lastName;
}
