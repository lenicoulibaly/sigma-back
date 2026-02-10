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
                d.association.assoId,
                d.association.assoName,
                d.section.sectionId,
                d.demandeur.userId,
                CONCAT(COALESCE(d.demandeur.firstName,''),' ',COALESCE(d.demandeur.lastName,'')),
                d.statut.code,
                d.statut.name,
                ws.color,
                ws.icon,
                d.message,
                d.dateSoumission,
                d.dateDecision,
                d.createdAt,
                CAST(NULL as string),
                d.adhesionCreee.adhesionId
            )
            FROM DemandeAdhesion d
            LEFT JOIN WorkflowStatus ws ON (ws.status.code = d.statut.code AND ws.workflow.code = 'DMD_ADH')
            WHERE (:assoId IS NULL OR d.association.assoId = :assoId)
              AND (
                :key IS NULL OR :key = '' OR
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

    @Query(value = """
            SELECT new lenicorp.metier.association.model.dtos.ReadDemandeAdhesionDTO(
                d.demandeId,
                d.association.assoId,
                d.association.assoName,
                d.section.sectionId,
                d.demandeur.userId,
                CONCAT(COALESCE(d.demandeur.firstName,''),' ',COALESCE(d.demandeur.lastName,'')),
                d.statut.code,
                d.statut.name,
                ws.color,
                ws.icon,
                d.message,
                d.dateSoumission,
                d.dateDecision,
                d.createdAt,
                CAST(NULL as string),
                d.adhesionCreee.adhesionId
            )
            FROM DemandeAdhesion d
            LEFT JOIN WorkflowStatus ws ON (ws.status.code = d.statut.code AND ws.workflow.code = 'DMD_ADH')
            WHERE (:userId IS NULL OR d.demandeur.userId = :userId)
              AND (COALESCE(:assoIds, NULL) IS NULL OR d.association.assoId IN (:assoIds))
              AND (
                :key IS NULL OR :key = '' OR
                UPPER(FUNCTION('unaccent', COALESCE(d.demandeur.firstName, ''))) LIKE CONCAT('%', :key, '%') OR
                UPPER(FUNCTION('unaccent', COALESCE(d.demandeur.lastName, ''))) LIKE CONCAT('%', :key, '%') OR
                UPPER(FUNCTION('unaccent', COALESCE(d.demandeur.email, ''))) LIKE CONCAT('%', :key, '%')
              )
              AND (
                :hasStatusFilter = false OR d.statut.code IN (:statutCodes)
              )
            ORDER BY d.dateSoumission DESC
            """,
            countQuery = """
            SELECT COUNT(d)
            FROM DemandeAdhesion d
            WHERE (:userId IS NULL OR d.demandeur.userId = :userId)
              AND (COALESCE(:assoIds, NULL) IS NULL OR d.association.assoId IN (:assoIds))
              AND (
                :key IS NULL OR :key = '' OR
                UPPER(FUNCTION('unaccent', COALESCE(d.demandeur.firstName, ''))) LIKE CONCAT('%', :key, '%') OR
                UPPER(FUNCTION('unaccent', COALESCE(d.demandeur.lastName, ''))) LIKE CONCAT('%', :key, '%') OR
                UPPER(FUNCTION('unaccent', COALESCE(d.demandeur.email, ''))) LIKE CONCAT('%', :key, '%')
              )
              AND (
                :hasStatusFilter = false OR d.statut.code IN (:statutCodes)
              )
            """)
    Page<ReadDemandeAdhesionDTO> searchForUser(
            @Param("userId") Long userId,
            @Param("key") String key,
            @Param("assoIds") List<Long> assoIds,
            @Param("statutCodes") List<String> statutCodes,
            @Param("hasStatusFilter") boolean hasStatusFilter,
            Pageable pageable);
}
