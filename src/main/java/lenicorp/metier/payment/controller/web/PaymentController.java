package lenicorp.metier.payment.controller.web;

import jakarta.validation.Valid;
import lenicorp.metier.payment.controller.services.PaymentService;
import lenicorp.metier.payment.model.dtos.PaymentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentDTO dto) {
        return new ResponseEntity<>(paymentService.createPayment(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> updatePayment(@PathVariable Long paymentId, @Valid @RequestBody PaymentDTO dto) {
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
            @RequestParam(required = false) String objectTypeCode,
            @RequestParam(required = false) Long objectId,
            @RequestParam(required = false) List<String> paymentModeCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String key,
            Pageable pageable) {
        return ResponseEntity.ok(paymentService.searchPayments(objectTypeCode, objectId, paymentModeCode, startDate, endDate, key, pageable));
    }
}
