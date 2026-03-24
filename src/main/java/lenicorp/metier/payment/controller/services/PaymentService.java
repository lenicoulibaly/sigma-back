package lenicorp.metier.payment.controller.services;

import lenicorp.metier.payment.controller.repositories.PaymentRepository;
import lenicorp.metier.payment.model.dtos.PaymentDTO;
import lenicorp.metier.payment.model.entities.Payment;
import lenicorp.metier.payment.model.mappers.PaymentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    public PaymentDTO createPayment(PaymentDTO dto) {
        Payment payment = paymentMapper.mapToEntity(dto);
        payment = paymentRepository.save(payment);
        return paymentMapper.mapToDto(payment);
    }

    public PaymentDTO updatePayment(Long paymentId, PaymentDTO dto) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'ID : " + paymentId));
        paymentMapper.updateEntity(dto, payment);
        payment = paymentRepository.save(payment);
        return paymentMapper.mapToDto(payment);
    }

    public void deletePayment(Long paymentId) {
        paymentRepository.deleteById(paymentId);
    }

    @Transactional(readOnly = true)
    public Page<PaymentDTO> searchPayments(String objectTypeCode, Long objectId, List<String> paymentModeCode, 
                                          LocalDate startDate, LocalDate endDate, String key, Pageable pageable) {
        return paymentRepository.searchPayments(objectTypeCode, objectId, paymentModeCode, startDate, endDate, key, pageable)
                .map(paymentMapper::mapToDto);
    }

    @Transactional(readOnly = true)
    public PaymentDTO getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .map(paymentMapper::mapToDto)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'ID : " + paymentId));
    }
}
