package lenicorp.metier.payment.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentBalanceDTO {
    private BigDecimal amountDue;
    private BigDecimal amountPaid;
    private BigDecimal remainingAmount;
    private boolean settled;
}
