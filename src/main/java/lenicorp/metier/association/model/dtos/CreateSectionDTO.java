package lenicorp.metier.association.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lenicorp.admin.structures.model.validators.ExistingStrId;
import lenicorp.metier.association.model.validators.ExistingAssoId;
import lenicorp.metier.association.model.validators.UniqueSectionName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@UniqueSectionName
public class CreateSectionDTO
{
    @NotNull(message = "Le nom de la section est obligatoire")
    @NotBlank(message = "Le nom de la section est obligatoire")
    private String sectionName;
    private String sigle;
    @ExistingAssoId
    @NotNull(message = "Veuillez selectionner l'association")
    private Long assoId;
    @ExistingStrId
    private Long strId;
    private String situationGeo;
    private String email;
    private String tel;
    private String adresse;
}