package lenicorp.metier.association.controller.services;

import lenicorp.metier.association.model.dtos.CreateDemandeAdhesionDTO;
import lenicorp.metier.association.model.dtos.ReadDemandeAdhesionDTO;
import lenicorp.metier.association.model.dtos.AdhesionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IDemandeAdhesionService {
    ReadDemandeAdhesionDTO create(CreateDemandeAdhesionDTO dto);
    ReadDemandeAdhesionDTO prendreEnEtude(Long demandeId);
    ReadDemandeAdhesionDTO approuver(Long demandeId);
    ReadDemandeAdhesionDTO confirmerPaiement(Long demandeId);
    ReadDemandeAdhesionDTO rejeter(Long demandeId, String motifRefus);
    ReadDemandeAdhesionDTO annuler(Long demandeId);

    Page<ReadDemandeAdhesionDTO> search(String key, Long assoId, List<String> statutCodes, Pageable pageable);

    // Crée un utilisateur avec profil, puis une demande d'adhésion et uploade ses documents
    ReadDemandeAdhesionDTO createUserWithDemandeAdhesion(AdhesionDTO adhesionDTO);
}
