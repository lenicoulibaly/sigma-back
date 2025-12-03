package lenicorp.metier.association.model.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.admin.security.model.dtos.UserProfileAssoDTO;
import lenicorp.admin.security.model.validators.*;
import lenicorp.admin.structures.model.validators.ExistingStrId;
import lenicorp.admin.types.model.validators.ExistingGradeCode;
import lenicorp.metier.association.model.validators.ExistingAdhesionId;
import lenicorp.metier.association.model.validators.ExistingAssoId;
import lenicorp.metier.association.model.validators.ExistingSectionId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AdhesionDTO
{
    private Long userId;
    @ExistingSectionId
    private Long sectionId;
    @ExistingAssoId
    @NotNull(message = "Veuillez selectionner l'association")
    private Long assoId;
    @ExistingAdhesionId
    private Long adhesionId;
    @UniqueMatricule
    private String matricule;
    private String nomCivilite;
    @ExistingGradeCode
    private String gradeCode;
    private Long indice;

    @NotNull(message = "Veuillez saisir le nom")
    @NotBlank(message = "Veuillez saisir le nom")
    private String firstName;
    @NotNull(message = "Veuillez saisir le prénom")
    @NotBlank(message = "Veuillez saisir le prénom")
    private String lastName;
    @Email(message = "Adresse mail invalide")
    @UniqueEmail
    @NotNull(message = "Le mail est obligatoire")
    private String email;
    @NotNull(message = "Veuillez saisir le numéro de téléphone")
    @NotBlank(message = "Veuillez saisir le numéro de téléphone")
    @UniqueTel
    private String tel;
    private String lieuNaissance;
    @Past(message = "La date de naissance ne peut être future")
    private LocalDate dateNaissance;
    @ValidCodeCivilite
    @NotNull(message = "La civilité est obligatoire")
    private String codeCivilite;
    private String sectionName;
    private String assoName;
    private boolean enabled;
    @ValidEmploiCode
    private String emploiCode;
    private String emploiName;
    @ExistingStrId
    private String strId;
    private String strName;
    private List<UploadDocReq> documents;
    private UserProfileAssoDTO profileDto;

    // Confirmations d'acceptation (alignées avec CreateDemandeAdhesionDTO)
    @AssertTrue(message = "Vous devez accepter le RGPD")
    private boolean accepteRgpd;
    @AssertTrue(message = "Vous devez accepter la charte d'adhésion")
    private boolean accepteCharte;

    public AdhesionDTO(Long userId, Long sectionId, Long assoId, Long adhesionId, String sectionName, String assoName) {
        this.userId = userId;
        this.sectionId = sectionId;
        this.assoId = assoId;
        this.adhesionId = adhesionId;
        this.sectionName = sectionName;
        this.assoName = assoName;
    }

    public AdhesionDTO(Long userId, String matriculeFonctionnaire, String email, String tel, String firstName, String lastName, String lieuNaissance, LocalDate dateNaissance, String codeCivilite, String nomCivilite) {
        this.userId = userId;
        this.matricule = matriculeFonctionnaire;
        this.email = email;
        this.tel = tel;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lieuNaissance = lieuNaissance;
        this.dateNaissance = dateNaissance;
        this.codeCivilite = codeCivilite;
        this.nomCivilite = nomCivilite;
    }

    public AdhesionDTO(Long userId, Long sectionId, Long assoId, Long adhesionId, String matriculeFonctionnaire, String nomCivilite, String codePays, String gradeCode, Long indiceFonctionnaire, String firstName, String lastName, String email, String tel, String lieuNaissance, LocalDate dateNaissance, String codeCivilite, String sectionName, String assoName) {
        this.userId = userId;
        this.sectionId = sectionId;
        this.assoId = assoId;
        this.adhesionId = adhesionId;
        this.matricule = matriculeFonctionnaire;
        this.nomCivilite = nomCivilite;
        this.gradeCode = gradeCode;
        this.indice = indiceFonctionnaire;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.tel = tel;
        this.lieuNaissance = lieuNaissance;
        this.dateNaissance = dateNaissance;
        this.codeCivilite = codeCivilite;
        this.sectionName = sectionName;
        this.assoName = assoName;
    }
}
