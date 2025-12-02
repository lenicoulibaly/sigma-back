package lenicorp.metier.association.model.dtos;

import jakarta.validation.constraints.NotNull;
import lenicorp.metier.association.model.validators.ExistingAssoId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UpdateAssociationDTO
{
    @ExistingAssoId
    @NotNull(message = "Veuillez selectionner l'association")
    private Long assoId;
    @NotNull(message = "Le nom de l'association ne peut être null")
    private String assoName;
    private String sigle;
    private String situationGeo;
    private BigDecimal droitAdhesion;
    private String email;
    private String tel;
    private String adresse;
}
