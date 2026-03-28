package lenicorp.metier.workflowadapters;

import lenicorp.admin.workflowengine.engine.adapter.ObjectAdapter;
import lenicorp.admin.workflowengine.execution.model.WorkflowTransitionLog;
import lenicorp.admin.workflowengine.execution.repo.WorkflowTransitionLogRepository;
import lenicorp.admin.workflowengine.model.dtos.InfoFieldDTO;
import lenicorp.admin.workflowengine.model.dtos.GeneralInfoOptions;
import lenicorp.metier.association.controller.repositories.DemandeAdhesionRepository;
import lenicorp.metier.association.model.entities.DemandeAdhesion;
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
public class DemandeAdhesionAdapter implements ObjectAdapter<DemandeAdhesion> {
    private final DemandeAdhesionRepository repository;
    private final WorkflowTransitionLogRepository wtLogRepo;
    private final IPaymentService paymentService;

    @Override
    public Class<DemandeAdhesion> targetType() {
        return DemandeAdhesion.class;
    }

    @Override
    public String getCurrentStatus(DemandeAdhesion obj) {
        return obj.getStatut() != null ? obj.getStatut().code : null;
    }

    @Override
    public void setStatus(DemandeAdhesion obj, String newStatus) {
        if (newStatus == null) {
            obj.setStatut(null);
        } else {
            obj.setStatut(new lenicorp.admin.types.model.entities.Type(newStatus));
        }
    }

    @Override
    public void setComment(DemandeAdhesion obj, String comment)
    {
        obj.setMotifStatut(comment);
    }

    @Override
    public Map<String, Object> toRuleMap(DemandeAdhesion obj) {
        Map<String, Object> facts = new HashMap<>();
        facts.put("droitAdhesion", obj.getAssociation() != null ? obj.getAssociation().getDroitAdhesion() : null);
        facts.put("association", obj.getAssociation() != null ? obj.getAssociation().getAssoName() : null);
        facts.put("section", obj.getSection() != null ? obj.getSection().getSectionName() : null);
        facts.put("accepteCharte", obj.getAccepteCharte());
        facts.put("accepteRgpd", obj.getAccepteRgpd());
        facts.put("accepteStatuts", obj.getAccepteStatutsReglements());
        facts.put("emailDemandeur", obj.getDemandeur().getEmail());
        facts.put("nomDemandeur", obj.getDemandeur().getFirstName() + " " + obj.getDemandeur().getLastName());
        facts.put("association", obj.getAssociation().getAssoName());
        return facts;
    }

    @Override
    public DemandeAdhesion load(String id) {
        try {
            return repository.findById(Long.valueOf(id)).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void save(DemandeAdhesion obj)
    {
        repository.save(obj);
    }

    @Override
    public String getId(DemandeAdhesion obj)
    {
        return obj.getDemandeId() != null ? obj.getDemandeId().toString() : null;
    }

    @Override
    public List<InfoFieldDTO> getGeneralInfo(DemandeAdhesion obj) {
        List<InfoFieldDTO> fields = new ArrayList<>();
        if (obj.getDemandeur() != null) {
            fields.add(new InfoFieldDTO("Demandeur", obj.getDemandeur().getFirstName() + " " + obj.getDemandeur().getLastName(), null));
        }
        if (obj.getAssociation() != null) {
            fields.add(new InfoFieldDTO("Association", obj.getAssociation().getAssoName(), null));
        }
        if (obj.getSection() != null) {
            fields.add(new InfoFieldDTO("Section", obj.getSection().getSectionName(), null));
        }
        fields.add(new InfoFieldDTO("Date Soumission", obj.getDateSoumission(), null));

        if (obj.getStatut() != null) {
            fields.add(new InfoFieldDTO("Statut Actuel", obj.getStatut().name, null));
        }
        return fields;
    }

    @Override
    public List<InfoFieldDTO> getGeneralInfo(DemandeAdhesion obj, GeneralInfoOptions options) {
        List<InfoFieldDTO> fields = new ArrayList<>(getGeneralInfo(obj));
        if (obj == null || obj.getDemandeId() == null) return fields;
        if (options == null || !options.isIncludePaymentInfo() || options.getPaymentTypeCode() == null) return fields;

        PaymentBalanceDTO bal;
        if (options.getChargeId() != null) {
            bal = paymentService.getPaymentBalanceByCharge(options.getChargeId());
        } else {
            Long carrierId = options.getChargeCarrierId() != null ? options.getChargeCarrierId() : obj.getDemandeId();
            boolean hasTargetTriplet = options.getPaymentTargetTypeCode() != null && options.getPaymentTargetId() != null && options.getPeriodKey() != null;
            if (hasTargetTriplet) {
                bal = paymentService.getPaymentBalance(options.getPaymentTypeCode(), carrierId,
                        options.getPaymentTargetTypeCode(), options.getPaymentTargetId(), options.getPeriodKey());
            } else {
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
