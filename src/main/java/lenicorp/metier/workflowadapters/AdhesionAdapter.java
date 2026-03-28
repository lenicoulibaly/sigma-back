package lenicorp.metier.workflowadapters;

import lenicorp.admin.workflowengine.engine.adapter.ObjectAdapter;
import lenicorp.admin.workflowengine.model.dtos.InfoFieldDTO;
import lenicorp.admin.workflowengine.model.dtos.GeneralInfoOptions;
import lenicorp.metier.association.controller.repositories.AdhesionRepo;
import lenicorp.metier.association.model.entities.Adhesion;
import lenicorp.metier.payment.controller.services.IPaymentService;
import lenicorp.metier.payment.model.dtos.PaymentBalanceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AdhesionAdapter implements ObjectAdapter<Adhesion> {
    private final AdhesionRepo repository;
    private final IPaymentService paymentService;

    @Override
    public Class<Adhesion> targetType() {
        return Adhesion.class;
    }

    @Override
    public String getCurrentStatus(Adhesion obj) {
        return obj.getStatut() != null ? obj.getStatut().code : null;
    }

    @Override
    public void setStatus(Adhesion obj, String newStatus) {
        if (newStatus == null) {
            obj.setStatut(null);
        } else {
            obj.setStatut(new lenicorp.admin.types.model.entities.Type(newStatus));
        }
    }

    @Override
    public void setComment(Adhesion obj, String comment)
    {
        //obj.setMotifStatut(comment);
    }

    @Override
    public Map<String, Object> toRuleMap(Adhesion obj) {
        Map<String, Object> facts = new HashMap<>();
        facts.put("active", obj.isActive());
        facts.put("userId", obj.getUserId());
        facts.put("association", obj.getAssociation() != null ? obj.getAssociation().getAssoName() : null);
        facts.put("section", obj.getSection() != null ? obj.getSection().getSectionName() : null);
        return facts;
    }

    @Override
    public Adhesion load(String id) {
        try {
            return repository.findById(Long.valueOf(id)).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void save(Adhesion obj) {
        repository.save(obj);
    }

    @Override
    public String getId(Adhesion obj) {
        return obj.getAdhesionId() != null ? obj.getAdhesionId().toString() : null;
    }

    @Override
    public List<InfoFieldDTO> getGeneralInfo(Adhesion obj) {
        List<InfoFieldDTO> fields = new ArrayList<>();
        fields.add(new InfoFieldDTO("ID Utilisateur", obj.getUserId(), null));
        if (obj.getAssociation() != null) {
            fields.add(new InfoFieldDTO("Association", obj.getAssociation().getAssoName(), null));
        }
        if (obj.getSection() != null) {
            fields.add(new InfoFieldDTO("Section", obj.getSection().getSectionName(), null));
        }
        fields.add(new InfoFieldDTO("État Actif", obj.isActive() ? "Oui" : "Non", null));
        if (obj.getStatut() != null) {
            fields.add(new InfoFieldDTO("Statut Actuel", obj.getStatut().name, null));
        }
        fields.add(new InfoFieldDTO("Date de création", obj.getCreatedAt(), null));

        return fields;
    }

    @Override
    public List<InfoFieldDTO> getGeneralInfo(Adhesion obj, GeneralInfoOptions options) {
        List<InfoFieldDTO> fields = new ArrayList<>(getGeneralInfo(obj));
        if (obj == null || obj.getAdhesionId() == null) return fields;
        if (options == null || !options.isIncludePaymentInfo() || options.getPaymentTypeCode() == null) return fields;

        PaymentBalanceDTO bal;
        if (options.getChargeId() != null) {
            bal = paymentService.getPaymentBalanceByCharge(options.getChargeId());
        } else {
            Long carrierId = options.getChargeCarrierId() != null ? options.getChargeCarrierId() : obj.getAdhesionId();
            boolean hasTargetTriplet = options.getPaymentTargetTypeCode() != null && options.getPaymentTargetId() != null && options.getPeriodKey() != null;
            if (hasTargetTriplet) {
                bal = paymentService.getPaymentBalance(options.getPaymentTypeCode(), carrierId,
                        options.getPaymentTargetTypeCode(), options.getPaymentTargetId(), options.getPeriodKey());
            } else {
                // Vue agrégée par porteur (toutes charges du type)
                bal = paymentService.getPaymentBalanceByCarrier(options.getPaymentTypeCode(), carrierId);
            }
        }

        fields.add(new InfoFieldDTO("Montant dû", bal.getAmountDue(), null));
        fields.add(new InfoFieldDTO("Montant payé", bal.getAmountPaid(), null));
        fields.add(new InfoFieldDTO("Reste à payer", bal.getRemainingAmount(), null));
        fields.add(new InfoFieldDTO("Soldé", bal.isSettled() ? "Oui" : "Non", null));
        return fields;
    }
}
