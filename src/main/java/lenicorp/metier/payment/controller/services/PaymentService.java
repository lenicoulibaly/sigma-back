package lenicorp.metier.payment.controller.services;

import lenicorp.admin.archive.controller.service.IDocumentService;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.metier.payment.controller.repositories.PaymentRepository;
import lenicorp.metier.payment.controller.repositories.ChargeRepository;
import lenicorp.metier.payment.controller.repositories.PaymentAllocationRepository;
import lenicorp.metier.payment.model.entities.Charge;
import lenicorp.metier.association.controller.repositories.DemandeAdhesionRepository;
import lenicorp.metier.association.model.entities.DemandeAdhesion;
import lenicorp.metier.payment.model.dtos.PaymentDTO;
import lenicorp.metier.payment.model.dtos.PaymentBalanceDTO;
import lenicorp.metier.payment.model.entities.Payment;
import lenicorp.metier.payment.model.mappers.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService implements IPaymentService
{
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final IDocumentService documentService;
    private final DemandeAdhesionRepository demandeAdhesionRepository;
    private final ChargeRepository chargeRepository;
    private final PaymentAllocationRepository paymentAllocationRepository;

    @Override
    public PaymentDTO createPayment(PaymentDTO dto) {
        Payment payment = paymentMapper.mapToEntity(dto);
        payment = paymentRepository.save(payment);

        // Upload attachments if provided
        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            for (UploadDocReq att : dto.getAttachments()) {
                if (att == null || att.getFile() == null) continue;
                att.setObjectId(payment.getPaymentId());
                att.setObjectTableName("PAYMENT");
                try {
                    documentService.uploadDocument(att);
                } catch (IOException e) {
                    throw new RuntimeException("Erreur lors de l'upload de la pièce jointe: " + e.getMessage(), e);
                }
            }
        }
        return paymentMapper.mapToDto(payment);
    }

    @Override
    public PaymentDTO updatePayment(Long paymentId, PaymentDTO dto) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'ID : " + paymentId));
        paymentMapper.updateEntity(dto, payment);
        payment = paymentRepository.save(payment);

        // Upload new attachments if provided
        if (dto.getAttachments() != null && !dto.getAttachments().isEmpty()) {
            for (UploadDocReq att : dto.getAttachments()) {
                if (att == null || att.getFile() == null) continue;
                att.setObjectId(payment.getPaymentId());
                att.setObjectTableName("PAYMENT");
                try {
                    documentService.uploadDocument(att);
                } catch (IOException e) {
                    throw new RuntimeException("Erreur lors de l'upload de la pièce jointe: " + e.getMessage(), e);
                }
            }
        }
        return paymentMapper.mapToDto(payment);
    }

    @Override
    public void deletePayment(Long paymentId) {
        paymentRepository.deleteById(paymentId);
    }

    @Transactional(readOnly = true)
    @Override
    public Page<PaymentDTO> searchPayments(String paymentTypeCode, Long objectId, List<String> paymentModeCode, 
                                          LocalDate startDate, LocalDate endDate, String key, Pageable pageable) {
        return paymentRepository.searchPayments(paymentTypeCode, objectId, paymentModeCode, startDate, endDate, key, pageable)
                .map(paymentMapper::mapToDto);
    }

    @Transactional(readOnly = true)
    @Override
    public PaymentDTO getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .map(paymentMapper::mapToDto)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'ID : " + paymentId));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPaidFor(String paymentTypeCode, Long objectId) {
        BigDecimal total = paymentRepository.sumAmountByTypeAndObject(paymentTypeCode, objectId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal computeDueFor(String paymentTypeCode, Long objectId) {
        if (paymentTypeCode == null || objectId == null) return BigDecimal.ZERO;
        // Cas particulier: demandes d'adhésion -> montant dû = droit d'adhésion de l'association
        if ("DMD_ADH".equalsIgnoreCase(paymentTypeCode)) {
            DemandeAdhesion dmd = demandeAdhesionRepository.findById(objectId).orElse(null);
            if (dmd != null && dmd.getAssociation() != null && dmd.getAssociation().getDroitAdhesion() != null) {
                return dmd.getAssociation().getDroitAdhesion();
            }
            return BigDecimal.ZERO;
        }
        // TODO: autres objets -> déterminer la source du montant dû
        return BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentBalanceDTO getPaymentBalance(String paymentTypeCode, Long objectId) {
        BigDecimal due = computeDueFor(paymentTypeCode, objectId);
        BigDecimal paid = getTotalPaidFor(paymentTypeCode, objectId);
        BigDecimal remaining = due.subtract(paid);
        if (remaining.signum() < 0) remaining = BigDecimal.ZERO; // pas de négatif
        return new PaymentBalanceDTO(due, paid, remaining, remaining.signum() == 0);
    }

    // --- Nouveau modèle centré Charge/Allocation ---
    @Override
    @Transactional(readOnly = true)
    public PaymentBalanceDTO getPaymentBalanceByCharge(Long chargeId) {
        Charge c = chargeRepository.findById(chargeId)
                .orElseThrow(() -> new RuntimeException("Charge introuvable: " + chargeId));
        BigDecimal due = c.getAmountDue() != null ? c.getAmountDue() : BigDecimal.ZERO;
        BigDecimal paid = paymentAllocationRepository.sumAllocatedByCharge(chargeId);
        if (paid == null) paid = BigDecimal.ZERO;
        BigDecimal remaining = due.subtract(paid);
        if (remaining.signum() < 0) remaining = BigDecimal.ZERO;
        return new PaymentBalanceDTO(due, paid, remaining, remaining.signum() == 0);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentBalanceDTO getPaymentBalance(String paymentTypeCode,
                                               Long chargeCarrierId,
                                               String paymentTargetTypeCode,
                                               Long paymentTargetId,
                                               String periodKey) {
        // Résolution de la charge (ciblée ou générique)
        Charge c;
        if (paymentTargetTypeCode != null && paymentTargetId != null && periodKey != null) {
            c = chargeRepository
                    .findByPaymentType_CodeAndChargeCarrierIdAndPaymentTargetType_CodeAndPaymentTargetIdAndPeriodKey(
                            paymentTypeCode, chargeCarrierId, paymentTargetTypeCode, paymentTargetId, periodKey)
                    .orElseThrow(() -> new RuntimeException("Charge introuvable pour le contexte fourni"));
        } else {
            c = chargeRepository.findGeneric(paymentTypeCode, chargeCarrierId)
                    .orElseThrow(() -> new RuntimeException("Charge générique introuvable pour le contexte fourni"));
        }
        return getPaymentBalanceByCharge(c.getChargeId());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentBalanceDTO getPaymentBalanceByCarrier(String paymentTypeCode, Long chargeCarrierId) {
        // Agrégation: somme due et payée sur toutes les charges génériques du porteur et/ou toutes les cibles ?
        // Choix: vue agrégée sur toutes les charges (toutes cibles/périodes) de ce porteur et type
        // Paid via allocation repo; Due en sommant les amountDue des charges correspondantes
        java.util.List<Charge> charges = chargeRepository.findAll( // simple, pourra être optimisé par une requête dédiée
                (root, query, cb) -> cb.and(
                        cb.equal(root.get("paymentType").get("code"), paymentTypeCode),
                        cb.equal(root.get("chargeCarrierId"), chargeCarrierId)
                )
        );
        BigDecimal due = charges.stream()
                .map(Charge::getAmountDue)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paid = paymentAllocationRepository.sumAllocatedByContext(paymentTypeCode, chargeCarrierId, null, null, null);
        if (paid == null) paid = BigDecimal.ZERO;
        BigDecimal remaining = due.subtract(paid);
        if (remaining.signum() < 0) remaining = BigDecimal.ZERO;
        return new PaymentBalanceDTO(due, paid, remaining, remaining.signum() == 0);
    }
}
