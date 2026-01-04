package lenicorp.metier.association.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class DemandeAdhesionDTO {
    private Long demandeId;
    private String reference;
    private Long associationId;
    private String associationNom;
    private Long sectionId;
    private String sectionNom;
    private Long demandeurId;
    private String demandeurNom;
    private String statutCode;
    private String statutNom;
    private LocalDateTime dateSoumission;
    private LocalDateTime dateDecision;
    private String motifRefus;
    private Boolean accepteCharte;
    private Boolean accepteRgpd;
    private Boolean accepteStatutsReglements;
    private String message;
    private BigDecimal montantCotisationEstime;
}
