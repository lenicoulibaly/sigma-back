package lenicorp.metier.payment.controller.repositories;

import lenicorp.metier.payment.model.entities.PaymentAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface PaymentAllocationRepository extends JpaRepository<PaymentAllocation, Long> {

    @Query("""
            SELECT COALESCE(SUM(a.amountAllocated), 0)
            FROM PaymentAllocation a
            WHERE a.charge.chargeId = :chargeId
            """)
    BigDecimal sumAllocatedByCharge(@Param("chargeId") Long chargeId);

    @Query("""
            SELECT COALESCE(SUM(a.amountAllocated), 0)
            FROM PaymentAllocation a
            JOIN a.charge c
            WHERE (:paymentTypeCode IS NULL OR c.paymentType.code = :paymentTypeCode)
              AND (:chargeCarrierId IS NULL OR c.chargeCarrierId = :chargeCarrierId)
              AND (:paymentTargetTypeCode IS NULL OR c.paymentTargetType.code = :paymentTargetTypeCode)
              AND (:paymentTargetId IS NULL OR c.paymentTargetId = :paymentTargetId)
              AND (:periodKey IS NULL OR c.periodKey = :periodKey)
            """)
    BigDecimal sumAllocatedByContext(@Param("paymentTypeCode") String paymentTypeCode,
                                     @Param("chargeCarrierId") Long chargeCarrierId,
                                     @Param("paymentTargetTypeCode") String paymentTargetTypeCode,
                                     @Param("paymentTargetId") Long paymentTargetId,
                                     @Param("periodKey") String periodKey);
}
