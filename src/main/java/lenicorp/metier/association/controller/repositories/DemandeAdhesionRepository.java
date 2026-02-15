package lenicorp.metier.association.controller.repositories;

import lenicorp.metier.association.model.dtos.DemandeAdhesionDTO;
import lenicorp.metier.association.model.entities.DemandeAdhesion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface DemandeAdhesionRepository extends JpaRepository<DemandeAdhesion, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DemandeAdhesion d WHERE d.demandeId = :id")
    Optional<DemandeAdhesion> lockById(@Param("id") Long id);

    @Query("""
            SELECT new lenicorp.metier.association.model.dtos.DemandeAdhesionDTO(
                d.demandeId,
                d.association.assoId,
                d.association.assoName,
                s.sectionId,
                s.sectionName,
                d.demandeur.userId,
                concat(d.demandeur.firstName, ' ', d.demandeur.lastName) ,
                d.statut.code,
                d.statut.name,
                ws.color,
                ws.icon,
                d.dateSoumission,
                d.dateDecision,
                d.createdAt,
                d.motifStatut,
                d.accepteCharte,
                d.accepteRgpd,
                d.accepteStatutsReglements,
                d.message
            )
            FROM DemandeAdhesion d left join d.section s
            left join WorkflowStatus ws on (ws.status.code = d.statut.code AND ws.workflow.code = 'DMD_ADH')
            WHERE (:associationId IS NULL OR d.association.assoId = :associationId)
            AND (:userId IS NULL OR d.demandeur.userId = :userId)
            AND (:key IS NULL 
                OR UPPER(FUNCTION('unaccent', d.association.assoName)) LIKE :key
                OR UPPER(FUNCTION('unaccent', d.demandeur.firstName)) LIKE :key
                OR UPPER(FUNCTION('unaccent', d.demandeur.lastName)) LIKE :key)
            AND (:statusCodes IS NULL OR d.statut.code IN :statusCodes)
            """)
    Page<DemandeAdhesionDTO> search(@Param("associationId") Long associationId,
                                    @Param("userId") Long userId,
                                    @Param("key") String key, 
                                    @Param("statusCodes") List<String> statusCodes, 
                                    Pageable pageable);

    @Query(value = """
            SELECT new lenicorp.metier.association.model.dtos.DemandeAdhesionDTO(
                d.demandeId,
                d.association.assoId,
                d.association.assoName,
                s.sectionId,
                s.sectionName,
                d.demandeur.userId,
                CONCAT(COALESCE(d.demandeur.firstName,''),' ',COALESCE(d.demandeur.lastName,'')),
                d.statut.code,
                d.statut.name,
                ws.color,
                ws.icon,
                d.dateSoumission,
                d.dateDecision,
                d.createdAt,
                d.motifStatut,
                d.accepteCharte,
                d.accepteRgpd,
                d.accepteStatutsReglements,
                d.message
            )
            FROM DemandeAdhesion d
            LEFT JOIN d.section s
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
    Page<DemandeAdhesionDTO> searchForUser(
            @Param("userId") Long userId,
            @Param("key") String key,
            @Param("assoIds") List<Long> assoIds,
            @Param("statutCodes") List<String> statutCodes,
            @Param("hasStatusFilter") boolean hasStatusFilter,
            Pageable pageable);

    @Query("""
            SELECT new lenicorp.metier.association.model.dtos.DemandeAdhesionDTO(
                d.demandeId,
                d.association.assoId,
                d.association.assoName,
                s.sectionId,
                s.sectionName,
                d.demandeur.userId,
                concat(d.demandeur.firstName, ' ', d.demandeur.lastName) ,
                d.statut.code,
                d.statut.name,
                ws.color,
                ws.icon,
                d.dateSoumission,
                d.dateDecision,
                d.createdAt,
                d.motifStatut,
                d.accepteCharte,
                d.accepteRgpd,
                d.accepteStatutsReglements,
                d.message
            )
            FROM DemandeAdhesion d left join d.section s
            left join WorkflowStatus ws on (ws.status.code = d.statut.code AND ws.workflow.code = 'DMD_ADH')
            WHERE d.demandeId = :id
            """)
    java.util.Optional<DemandeAdhesionDTO> findByIdCustom(@Param("id") Long id);
}
