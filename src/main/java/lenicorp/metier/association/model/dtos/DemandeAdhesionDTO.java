package lenicorp.metier.association.model.dtos;

import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class DemandeAdhesionDTO {
    private Long demandeId;
    private Long assoId;
    private String assoName;
    private Long sectionId;
    private String sectionName;
    private Long demandeurId;
    private String demandeurNom;
    private String statutCode;
    private String statutNom;
    private String statutColor;
    private String statutIcon;
    private LocalDateTime dateSoumission;
    private LocalDateTime dateDecision;
    private LocalDateTime createdAt;
    private String motifStatut;
    private Boolean accepteCharte;
    private Boolean accepteRgpd;
    private Boolean accepteStatutsReglements;
    private String message;
    private List<UploadDocReq> documents;

    public DemandeAdhesionDTO(Long demandeId, Long assoId, String associationNom, Long sectionId, String sectionNom, Long demandeurId, String demandeurNom, String statutCode, String statutNom, String statutColor, String statutIcon, LocalDateTime dateSoumission, LocalDateTime dateDecision, LocalDateTime createdAt, String motifStatut, Boolean accepteCharte, Boolean accepteRgpd, Boolean accepteStatutsReglements, String message)
    {
        this.demandeId = demandeId;
        this.assoId = assoId;
        this.assoName = associationNom;
        this.sectionId = sectionId;
        this.sectionName = sectionNom;
        this.demandeurId = demandeurId;
        this.demandeurNom = demandeurNom;
        this.statutCode = statutCode;
        this.statutNom = statutNom;
        this.statutColor = statutColor;
        this.statutIcon = statutIcon;
        this.dateSoumission = dateSoumission;
        this.dateDecision = dateDecision;
        this.createdAt = createdAt;
        this.motifStatut = motifStatut;
        this.accepteCharte = accepteCharte;
        this.accepteRgpd = accepteRgpd;
        this.accepteStatutsReglements = accepteStatutsReglements;
        this.message = message;
    }
}
