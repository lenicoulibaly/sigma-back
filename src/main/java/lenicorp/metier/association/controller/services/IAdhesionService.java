package lenicorp.metier.association.controller.services;

import jakarta.transaction.Transactional;
import lenicorp.admin.archive.model.dtos.response.ReadDocDTO;
import lenicorp.admin.archive.model.entities.Document;
import lenicorp.admin.utilities.SelectOption;
import lenicorp.metier.association.model.dtos.AdhesionDTO;
import lenicorp.metier.association.model.entities.Adhesion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IAdhesionService
{
    Adhesion createUserAndAdhesion(AdhesionDTO dto);

    @Transactional
    Adhesion updateMembre(AdhesionDTO dto);

    void seDesabonner(Long adhesionId);
    Page<AdhesionDTO> searchAdhsions(String key, Long assoId, Long sectionId, Pageable pageable);
    AdhesionDTO getMembreDTO(String uniqueIdentifier);
    List<SelectOption> getOptions(Long assoId);

    Optional<Adhesion> findByEmailAndSection(String email, Long sectionId);

    Optional<Adhesion> findByEmailAndAsso(String email, Long assoId);

    void createAdhesionFromDemande(Long demandeId);

    Page<ReadDocDTO> searchObjectDocs(Long objectId, String key, Pageable pageable);
}
