package lenicorp.metier.payment.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payment_allocation",
       uniqueConstraints = @UniqueConstraint(name = "UK_ALLOC_UNIQ", columnNames = {"PAY_ID", "CHG_ID"}))
@Audited
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentAllocation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ALLOC_ID_GEN")
    @SequenceGenerator(name = "ALLOC_ID_GEN", sequenceName = "ALLOC_ID_GEN")
    @Column(name = "ALLOC_ID")
    private Long allocationId;

    @ManyToOne
    @JoinColumn(name = "PAY_ID", nullable = false)
    private Payment payment;

    @ManyToOne
    @JoinColumn(name = "CHG_ID", nullable = false)
    private Charge charge;

    @Column(name = "AMOUNT_ALLOCATED", nullable = false)
    private BigDecimal amountAllocated;

    @Column(name = "ALLOCATED_AT")
    private LocalDate allocatedAt;
}
