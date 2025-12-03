package lenicorp.metier.association.model.entities;

import jakarta.persistence.*;
import lenicorp.admin.security.audit.AuditableEntity;
import lenicorp.admin.security.model.entities.AppUser;
import lenicorp.admin.types.model.entities.Type;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Audited @Getter @Setter @NoArgsConstructor
public class DemandeAdhesion extends AuditableEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEMANDE_ADHESION_ID_GEN")
    @SequenceGenerator(name = "DEMANDE_ADHESION_ID_GEN", sequenceName = "DEMANDE_ADHESION_ID_GEN", allocationSize = 10)
    private Long demandeId;

    @Column(length = 50)
    private String reference;

    @ManyToOne @JoinColumn(name = "ASSO_ID")
    private Association association;

    @ManyToOne @JoinColumn(name = "SECTION_ID")
    private Section section; // nullable

    @ManyToOne @JoinColumn(name = "USER_ID")
    private AppUser user;

    @ManyToOne @JoinColumn(name = "STATUT_CODE")
    private Type statut; // EN_ATTENTE, EN_ETUDE, COMPLEMENTS_REQUIS, APPROUVEE, REJETEE, ANNULEE

    private LocalDateTime dateSoumission;
    private LocalDateTime dateDecision;
    @Column(length = 2000)
    private String motifRefus;
    private Boolean accepteCharte;
    private Boolean accepteRgpd;
    private Boolean accepteStatutsReglements;

    @Column(length = 4000)
    private String message;

    private BigDecimal montantCotisationEstime;

    @OneToOne @JoinColumn(name = "ADHESION_ID")
    private Adhesion adhesionCreee; // renseigné si APPROUVEE
}