package lenicorp.metier.association.controller.repositories;

import lenicorp.metier.association.model.dtos.ReadDemandeAdhesionDTO;
import lenicorp.metier.association.model.entities.DemandeAdhesion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface DemandeAdhesionRepo extends JpaRepository<DemandeAdhesion, Long>
{
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DemandeAdhesion d WHERE d.demandeId = :id")
    Optional<DemandeAdhesion> lockById(@Param("id") Long id);

    @Query(value = """
            SELECT new lenicorp.metier.association.model.dtos.ReadDemandeAdhesionDTO(
                d.demandeId,
                d.reference,
                d.association.assoId,
                d.section.sectionId,
                d.demandeur.userId,
                CONCAT(COALESCE(d.demandeur.firstName,''),' ',COALESCE(d.demandeur.lastName,'')),
                d.statut.code,
                d.statut.name,
                d.message,
                d.dateSoumission,
                d.dateDecision,
                NULL,
                d.montantCotisationEstime,
                d.adhesionCreee.adhesionId
            )
            FROM DemandeAdhesion d
            WHERE (:assoId IS NULL OR d.association.assoId = :assoId)
              AND (
                :key IS NULL OR :key = '' OR
                UPPER(FUNCTION('unaccent', COALESCE(d.reference, ''))) LIKE CONCAT('%', :key, '%') OR
                UPPER(FUNCTION('unaccent', COALESCE(d.demandeur.firstName, ''))) LIKE CONCAT('%', :key, '%') OR
                UPPER(FUNCTION('unaccent', COALESCE(d.demandeur.lastName, ''))) LIKE CONCAT('%', :key, '%') OR
                UPPER(FUNCTION('unaccent', COALESCE(d.demandeur.email, ''))) LIKE CONCAT('%', :key, '%')
              )
              AND (
                :hasStatusFilter = false OR d.statut.code IN (:statutCodes)
              )
            ORDER BY d.dateSoumission ASC
            """,
            countQuery = """
            SELECT COUNT(d)
            FROM DemandeAdhesion d
            WHERE (:assoId IS NULL OR d.association.assoId = :assoId)
              AND (
                :key IS NULL OR :key = '' OR
                UPPER(FUNCTION('unaccent', COALESCE(d.reference, ''))) LIKE CONCAT('%', :key, '%') OR
                UPPER(FUNCTION('unaccent', COALESCE(d.demandeur.firstName, ''))) LIKE CONCAT('%', :key, '%') OR
                UPPER(FUNCTION('unaccent', COALESCE(d.demandeur.lastName, ''))) LIKE CONCAT('%', :key, '%') OR
                UPPER(FUNCTION('unaccent', COALESCE(d.demandeur.email, ''))) LIKE CONCAT('%', :key, '%')
              )
              AND (
                :hasStatusFilter = false OR d.statut.code IN (:statutCodes)
              )
            """)
    Page<ReadDemandeAdhesionDTO> search(
            @Param("key") String key,
            @Param("assoId") Long assoId,
            @Param("statutCodes") List<String> statutCodes,
            @Param("hasStatusFilter") boolean hasStatusFilter,
            Pageable pageable);
}
