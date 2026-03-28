package lenicorp.admin.workflowengine.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneralInfoOptions {
    private boolean includePaymentInfo;
    private String paymentTypeCode;

    // Contexte de résolution des soldes (au choix)
    // 1) Direct via chargeId
    private Long chargeId;

    // 2) Via clé logique (tous NULL ou tous NON NULL pour le triplet cible)
    private Long chargeCarrierId;          // ex. adhesionId, demandeId, souscriptionId
    private String paymentTargetTypeCode;  // ex. COTISATION, APPEL_FONDS, ECHEANCE_PRET
    private Long paymentTargetId;          // ex. echeanceId, appelFondsId, ligneId
    private String periodKey;              // ex. 2026-03 ou NA
}
