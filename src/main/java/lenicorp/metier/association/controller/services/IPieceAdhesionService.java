package lenicorp.metier.association.controller.services;

import lenicorp.metier.association.model.dtos.PieceAdhesionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPieceAdhesionService {
    PieceAdhesionDTO create(PieceAdhesionDTO dto);
    PieceAdhesionDTO update(PieceAdhesionDTO dto);
    Page<PieceAdhesionDTO> search(String key, Long assoId, Pageable pageable);
}
