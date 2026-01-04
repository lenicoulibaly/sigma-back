package lenicorp.metier.workflowadapters;

import lenicorp.admin.workflowengine.engine.adapter.ObjectAdapter;
import lenicorp.metier.association.model.entities.DemandeAdhesion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DemandeAdhesionAdapter implements ObjectAdapter<DemandeAdhesion> {
    private final lenicorp.metier.association.controller.repositories.DemandeAdhesionRepository repository;

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
    public Map<String, Object> toRuleMap(DemandeAdhesion obj) {
        Map<String, Object> facts = new HashMap<>();
        facts.put("droitAdhesion", obj.getAssociation() != null ? obj.getAssociation().getDroitAdhesion() : null);
        facts.put("montant", obj.getMontantCotisationEstime());
        facts.put("reference", obj.getReference());
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
    public void save(DemandeAdhesion obj) {
        repository.save(obj);
    }

    @Override
    public String getId(DemandeAdhesion obj) {
        return obj.getDemandeId() != null ? obj.getDemandeId().toString() : null;
    }
}
