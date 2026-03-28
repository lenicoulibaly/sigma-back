package lenicorp.metier.payment.model.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;
import lenicorp.metier.payment.model.validators.ExistingModePaiement;
import lenicorp.metier.payment.model.validators.ExistingTypePaiement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long paymentId;
    
    @NotBlank(message = "La référence est obligatoire")
    private String reference;
    
    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal amount;
    
    @NotNull(message = "La date de paiement est obligatoire")
    private LocalDate paymentDate;
    
    private String description;
    
    // Codes pour les relations
    @NotBlank(message = "Le mode de paiement est obligatoire")
    @ExistingModePaiement(message = "Mode de paiement invalide")
    private String paymentModeCode;
    private String paymentModeName;
    
    @NotBlank(message = "Le type de paiement est obligatoire")
    @ExistingTypePaiement(message = "Type de paiement invalide")
    private String paymentTypeCode;
    private String paymentTypeName;
    
    @NotNull(message = "L'ID de l'objet est obligatoire")
    private Long objectId;

    private List<UploadDocReq> attachments = new ArrayList<>();

    // Champs d'audit (optionnel, mais souvent utile dans les DTOs de retour)
    private LocalDateTime createdAt;
    private String createdBy;
}
