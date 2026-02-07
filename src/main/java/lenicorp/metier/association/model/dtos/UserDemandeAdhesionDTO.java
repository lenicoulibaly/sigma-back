package lenicorp.metier.association.model.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.admin.security.model.validators.*;
import lenicorp.admin.structures.model.validators.ExistingStrId;
import lenicorp.admin.types.model.validators.ExistingGradeCode;
import lenicorp.metier.association.model.validators.ExistingAssoId;
import lenicorp.metier.association.model.validators.ExistingSectionId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserDemandeAdhesionDTO {
    // Champs utilisateur (depuis AdhesionDTO)
    @UniqueMatricule
    private String matricule;
    
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
    
    @ExistingGradeCode
    private String gradeCode;
    
    private Long indice;
    
    @ValidEmploiCode
    private String emploiCode;
    
    @ExistingStrId
    private Long strId;

    // Champs DemandeAdhesion
    private Long demandeId;
    
    @ExistingAssoId
    @NotNull(message = "Veuillez sélectionner l'association")
    private Long assoId;
    
    @ExistingSectionId
    private Long sectionId;
    
    private Long demandeurId;
    private Boolean accepteCharte;
    private Boolean accepteRgpd;
    private Boolean accepteStatutsReglements;
    private String message;
    private List<UploadDocReq> documents;
}
