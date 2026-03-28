package lenicorp.metier.payment.controller.services;

import lenicorp.metier.payment.model.dtos.PaymentBalanceDTO;
import lenicorp.metier.payment.model.dtos.PaymentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IPaymentService {

    PaymentDTO createPayment(PaymentDTO dto);

    PaymentDTO updatePayment(Long paymentId, PaymentDTO dto);

    void deletePayment(Long paymentId);

    PaymentDTO getPayment(Long paymentId);

    Page<PaymentDTO> searchPayments(String paymentTypeCode, Long objectId, List<String> paymentModeCode,
                                    LocalDate startDate, LocalDate endDate, String key, Pageable pageable);

    BigDecimal getTotalPaidFor(String paymentTypeCode, Long objectId);

    BigDecimal computeDueFor(String paymentTypeCode, Long objectId);

    PaymentBalanceDTO getPaymentBalance(String paymentTypeCode, Long objectId);

    // Nouveau modèle centré Charge/Allocation
    PaymentBalanceDTO getPaymentBalanceByCharge(Long chargeId);

    PaymentBalanceDTO getPaymentBalance(String paymentTypeCode,
                                        Long chargeCarrierId,
                                        String paymentTargetTypeCode,
                                        Long paymentTargetId,
                                        String periodKey);

    // Agrégé par porteur uniquement (cas générique: pas de triplet cible)
    PaymentBalanceDTO getPaymentBalanceByCarrier(String paymentTypeCode, Long chargeCarrierId);
}
