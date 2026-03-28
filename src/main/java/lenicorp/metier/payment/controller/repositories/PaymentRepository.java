package lenicorp.metier.payment.controller.repositories;

import lenicorp.metier.payment.model.entities.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
            SELECT p FROM Payment p
            WHERE (:paymentTypeCode IS NULL OR p.paymentType.code = :paymentTypeCode)
            AND (:objectId IS NULL OR p.objectId = :objectId)
            AND (:paymentModeCode IS NULL OR p.paymentMode.code IN :paymentModeCode)
            AND (:startDate IS NULL OR p.paymentDate >= :startDate)
            AND (:endDate IS NULL OR p.paymentDate <= :endDate)
            AND (:key IS NULL OR UPPER(FUNCTION('unaccent', COALESCE(p.reference, ''))) LIKE UPPER(FUNCTION('unaccent', CONCAT('%', :key, '%')))
                OR UPPER(FUNCTION('unaccent', COALESCE(p.description, ''))) LIKE UPPER(FUNCTION('unaccent', CONCAT('%', :key, '%'))))
            """)
    Page<Payment> searchPayments(
            @Param("paymentTypeCode") String paymentTypeCode,
            @Param("objectId") Long objectId,
            @Param("paymentModeCode") List<String> paymentModeCode,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("key") String key,
            Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(p.amount), 0)
            FROM Payment p
            WHERE (:paymentTypeCode IS NULL OR p.paymentType.code = :paymentTypeCode)
              AND (:objectId IS NULL OR p.objectId = :objectId)
            """)
    java.math.BigDecimal sumAmountByTypeAndObject(@Param("paymentTypeCode") String paymentTypeCode,
                                                  @Param("objectId") Long objectId);
}
