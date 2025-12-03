package lenicorp.metier.association.controller.services;

import lenicorp.metier.association.model.dtos.DemandeAdhesionCreateDTO;
import lenicorp.metier.association.model.dtos.DemandeAdhesionReadDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IDemandeAdhesionService {
    DemandeAdhesionReadDTO create(DemandeAdhesionCreateDTO dto);
    DemandeAdhesionReadDTO prendreEnEtude(Long demandeId);
    DemandeAdhesionReadDTO approuver(Long demandeId);
    DemandeAdhesionReadDTO confirmerPaiement(Long demandeId);
    DemandeAdhesionReadDTO rejeter(Long demandeId, String motifRefus);
    DemandeAdhesionReadDTO annuler(Long demandeId);

    Page<DemandeAdhesionReadDTO> search(String key, Long assoId, List<String> statutCodes, Pageable pageable);
}
