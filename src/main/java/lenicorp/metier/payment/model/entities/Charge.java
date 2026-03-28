package lenicorp.metier.payment.model.entities;

import jakarta.persistence.*;
import lenicorp.admin.security.audit.AuditableEntity;
import lenicorp.admin.types.model.entities.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import org.hibernate.envers.Audited;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "charge",
        uniqueConstraints = @UniqueConstraint(
                name = "UK_CHARGE_CTX",
                columnNames = {"PAYMENT_TYPE_CODE", "CHARGE_CARRIER_ID", "PAYMENT_TARGET_TYPE_CODE", "PAYMENT_TARGET_ID", "PERIOD_KEY"}
        )
)
@Check(
        constraints = "(payment_target_id IS NULL AND payment_target_type_code IS NULL AND period_key IS NULL) OR (payment_target_id IS NOT NULL AND payment_target_type_code IS NOT NULL AND period_key IS NOT NULL)"
)
@Audited
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Charge extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHG_ID_GEN")
    @SequenceGenerator(name = "CHG_ID_GEN", sequenceName = "CHG_ID_GEN")
    @Column(name = "CHG_ID")
    private Long chargeId;

    @ManyToOne
    @JoinColumn(name = "PAYMENT_TYPE_CODE", nullable = false)
    private Type paymentType; // ex: DMD_ADH, COTIS, PRET_REMBOURSEMENT, ...

    @Column(name = "CHARGE_CARRIER_ID", nullable = false)
    private Long chargeCarrierId; // Agrégat porteur: Adhesion/Demande/... ID

    @ManyToOne
    @JoinColumn(name = "PAYMENT_TARGET_TYPE_CODE")
    private Type paymentTargetType; // Type fonctionnel de la cible (peut être NULL si cas générique)

    @Column(name = "PAYMENT_TARGET_ID")
    private Long paymentTargetId; // ID de la cible précise (échéance/appel/ligne...) (peut être NULL)

    @Column(name = "PERIOD_KEY", length = 20)
    private String periodKey; // ex: 2026, 2026-03 (peut être NULL)

    @Column(name = "PERIOD_START")
    private LocalDate periodStart;

    @Column(name = "PERIOD_END")
    private LocalDate periodEnd;

    @Column(name = "AMOUNT_DUE", nullable = false)
    private BigDecimal amountDue;

    @Column(name = "DUE_DATE")
    private LocalDate dueDate;

    @ManyToOne
    @JoinColumn(name = "STATUS_CODE")
    private Type status; // POSTED, PARTIALLY_SETTLED, SETTLED, CANCELED

    @Column(name = "POSTED_AT")
    private LocalDate postedAt;

    @Column(name = "SETTLED_AT")
    private LocalDate settledAt;

    @Column(name = "REFERENCE", unique = true)
    private String reference;

    @Column(name = "LABEL", length = 500)
    private String label;
}
