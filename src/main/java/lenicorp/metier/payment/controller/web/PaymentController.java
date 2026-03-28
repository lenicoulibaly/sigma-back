package lenicorp.metier.payment.controller.web;

import jakarta.validation.Valid;
import lenicorp.metier.payment.controller.services.IPaymentService;
import lenicorp.metier.payment.model.dtos.PaymentDTO;
import lenicorp.metier.payment.model.dtos.PaymentBalanceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PaymentDTO> createPayment(@Valid @ModelAttribute PaymentDTO dto) {
        return new ResponseEntity<>(paymentService.createPayment(dto), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{paymentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PaymentDTO> updatePayment(@PathVariable Long paymentId,
                                                    @Valid @ModelAttribute PaymentDTO dto) {
        return ResponseEntity.ok(paymentService.updatePayment(paymentId, dto));
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long paymentId) {
        paymentService.deletePayment(paymentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPayment(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPayment(paymentId));
    }

    @GetMapping
    public ResponseEntity<Page<PaymentDTO>> searchPayments(
            @RequestParam(required = false) String paymentTypeCode,
            @RequestParam(required = false) Long objectId,
            @RequestParam(required = false) List<String> paymentModeCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String key,
            Pageable pageable) {
        return ResponseEntity.ok(paymentService.searchPayments(paymentTypeCode, objectId, paymentModeCode, startDate, endDate, key, pageable));
    }
    
    // Legacy: agrégé par objet (ancien modèle)
    @GetMapping("/balance")
    public ResponseEntity<PaymentBalanceDTO> getLegacyBalance(@RequestParam String paymentTypeCode,
                                                              @RequestParam Long objectId) {
        return ResponseEntity.ok(paymentService.getPaymentBalance(paymentTypeCode, objectId));
    }

    // Nouveau: par chargeId direct
    @GetMapping("/balance/by-charge")
    public ResponseEntity<PaymentBalanceDTO> getBalanceByCharge(@RequestParam Long chargeId) {
        return ResponseEntity.ok(paymentService.getPaymentBalanceByCharge(chargeId));
    }

    // Nouveau: par clé logique complète
    @GetMapping("/balance/by-context")
    public ResponseEntity<PaymentBalanceDTO> getBalanceByContext(@RequestParam String paymentTypeCode,
                                                                 @RequestParam Long chargeCarrierId,
                                                                 @RequestParam(required = false) String paymentTargetTypeCode,
                                                                 @RequestParam(required = false) Long paymentTargetId,
                                                                 @RequestParam(required = false) String periodKey) {
        return ResponseEntity.ok(
                paymentService.getPaymentBalance(paymentTypeCode, chargeCarrierId, paymentTargetTypeCode, paymentTargetId, periodKey)
        );
    }
    // Les fichiers doivent être envoyés dans dto.attachments[*].file via @ModelAttribute
}
