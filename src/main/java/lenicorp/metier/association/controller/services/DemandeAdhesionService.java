package lenicorp.metier.association.controller.services;

import lenicorp.metier.association.model.dtos.DemandeAdhesionDTO;
import lenicorp.metier.association.model.dtos.ReadDemandeAdhesionDTO;
import lenicorp.metier.association.model.dtos.UserDemandeAdhesionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DemandeAdhesionService {
    DemandeAdhesionDTO create(DemandeAdhesionDTO dto);
    DemandeAdhesionDTO createUserAndDemandeAdhesion(UserDemandeAdhesionDTO dto);
    DemandeAdhesionDTO update(Long id, DemandeAdhesionDTO dto);
    Page<DemandeAdhesionDTO> search(Long associationId, Long userId, String key, String workflowStatusGroupCode, Pageable pageable);
    Page<ReadDemandeAdhesionDTO> searchForUser(Long userId, String key, List<Long> assoIds, List<String> workflowStatusGroupCodes, Pageable pageable);
}
