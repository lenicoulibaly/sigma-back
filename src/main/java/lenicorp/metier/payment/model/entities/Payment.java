package lenicorp.metier.payment.model.entities;

import jakarta.persistence.*;
import lenicorp.admin.security.audit.AuditableEntity;
import lenicorp.admin.types.model.entities.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data 
@NoArgsConstructor 
@AllArgsConstructor
@Audited
@Table(name = "payment")
public class Payment extends AuditableEntity {

    @Id 
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAY_ID_GEN")
    @SequenceGenerator(name = "PAY_ID_GEN", sequenceName = "PAY_ID_GEN")
    @Column(name = "pay_id")
    private Long paymentId;

    @Column(name = "pay_reference", unique = true)
    private String reference;

    @Column(name = "pay_amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "pay_date")
    private LocalDate paymentDate;

    @Column(name = "pay_description", length = 1000)
    private String description;

    // Le mode de paiement (Espèces, Virement, Chèque, etc.)
    @ManyToOne 
    @JoinColumn(name = "PAY_MODE_CODE") 
    @NotAudited
    private Type paymentMode;

    // --- Champs pour la liaison générique ---

    // Le type d'objet concerné (ex: FACTURE, COMMANDE, DOSSIER, etc.)
    @ManyToOne 
    @JoinColumn(name = "OBJECT_TYPE_CODE") 
    @NotAudited
    private Type objectType;

    // L'ID de l'objet auquel appartient le paiement
    @Column(name = "object_id")
    private Long objectId; 
}
