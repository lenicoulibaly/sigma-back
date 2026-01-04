package lenicorp.metier.association.controller.repositories;

import lenicorp.metier.association.model.dtos.DemandeAdhesionDTO;
import lenicorp.metier.association.model.entities.DemandeAdhesion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandeAdhesionRepository extends JpaRepository<DemandeAdhesion, Long> {

    @Query("""
            SELECT new lenicorp.metier.association.model.dtos.DemandeAdhesionDTO(
                d.demandeId,
                d.reference,
                d.association.assoId,
                d.association.assoName,
                d.section.sectionId,
                d.section.sectionName,
                d.demandeur.userId,
                d.demandeur.firstName,
                d.statut.code,
                d.statut.name,
                d.dateSoumission,
                d.dateDecision,
                d.motifRefus,
                d.accepteCharte,
                d.accepteRgpd,
                d.accepteStatutsReglements,
                d.message,
                d.montantCotisationEstime
            )
            FROM DemandeAdhesion d
            WHERE (:key IS NULL 
                OR UPPER(FUNCTION('unaccent', d.reference)) LIKE :key
                OR UPPER(FUNCTION('unaccent', d.association.assoName)) LIKE :key
                OR UPPER(FUNCTION('unaccent', d.demandeur.firstName)) LIKE :key
                OR UPPER(FUNCTION('unaccent', d.demandeur.lastName)) LIKE :key)
            AND (:statusCodes IS NULL OR d.statut.code IN :statusCodes)
            """)
    Page<DemandeAdhesionDTO> search(@Param("key") String key, 
                                    @Param("statusCodes") List<String> statusCodes, 
                                    Pageable pageable);
}
