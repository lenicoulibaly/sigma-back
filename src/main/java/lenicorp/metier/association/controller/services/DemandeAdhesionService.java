package lenicorp.metier.association.controller.services;

import lenicorp.metier.association.model.dtos.DemandeAdhesionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DemandeAdhesionService {
    DemandeAdhesionDTO create(DemandeAdhesionDTO dto);
    DemandeAdhesionDTO update(Long id, DemandeAdhesionDTO dto);
    Page<DemandeAdhesionDTO> search(String key, String workflowStatusGroupCode, Pageable pageable);
}
