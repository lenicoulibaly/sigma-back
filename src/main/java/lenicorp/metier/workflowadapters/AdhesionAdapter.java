package lenicorp.metier.workflowadapters;

import lenicorp.admin.workflowengine.engine.adapter.ObjectAdapter;
import lenicorp.admin.workflowengine.model.dtos.InfoFieldDTO;
import lenicorp.metier.association.controller.repositories.AdhesionRepo;
import lenicorp.metier.association.model.entities.Adhesion;
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
}
