package lenicorp.metier.payment.controller.repositories;

import lenicorp.metier.payment.model.entities.Charge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChargeRepository extends JpaRepository<Charge, Long>, JpaSpecificationExecutor<Charge> {

    Optional<Charge> findByPaymentType_CodeAndChargeCarrierIdAndPaymentTargetType_CodeAndPaymentTargetIdAndPeriodKey(
            String paymentTypeCode, Long chargeCarrierId, String paymentTargetTypeCode, Long paymentTargetId, String periodKey);

    @Query("""
            SELECT c FROM Charge c
            WHERE c.paymentType.code = :paymentTypeCode
              AND c.chargeCarrierId = :chargeCarrierId
              AND c.paymentTargetType IS NULL AND c.paymentTargetId IS NULL AND c.periodKey IS NULL
            """)
    Optional<Charge> findGeneric(@Param("paymentTypeCode") String paymentTypeCode,
                                 @Param("chargeCarrierId") Long chargeCarrierId);
}
