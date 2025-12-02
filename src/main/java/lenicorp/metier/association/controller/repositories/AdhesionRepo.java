package lenicorp.metier.association.controller.repositories;

import lenicorp.metier.association.model.dtos.AdhesionDTO;
import lenicorp.metier.association.model.entities.Adhesion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AdhesionRepo extends JpaRepository<Adhesion, Long>
{
    @Query(value = """
            SELECT NEW lenicorp.metier.association.model.dtos.AdhesionDTO(
                CAST(a.userId AS long), sect.sectionId, asso.assoId, a.adhesionId, sect.sectionName, asso.assoName) 
            FROM Adhesion a 
            LEFT JOIN a.section sect 
            LEFT JOIN sect.association sectAsso 
            LEFT JOIN a.association asso 
            WHERE 
            (
                UPPER(FUNCTION('unaccent', COALESCE(sect.sectionName, ''))) LIKE :key 
                OR UPPER(FUNCTION('unaccent', COALESCE(asso.assoName, ''))) LIKE :key 
            ) 
            AND ((asso.assoId = :assoId OR sectAsso.assoId = :assoId) OR (sect.sectionId = :sectionId))
            """,
            countQuery = """
            SELECT COUNT(a) 
            FROM Adhesion a 
            LEFT JOIN a.section sect 
            LEFT JOIN sect.association sectAsso 
            LEFT JOIN a.association asso 
            WHERE 
            (
                UPPER(FUNCTION('unaccent', COALESCE(sect.sectionName, ''))) LIKE :key 
                OR UPPER(FUNCTION('unaccent', COALESCE(asso.assoName, ''))) LIKE :key 
            ) 
            AND ((asso.assoId = :assoId OR sectAsso.assoId = :assoId) OR (sect.sectionId = :sectionId))
            """)
    Page<AdhesionDTO> searchAdhesions(@Param("key") String key, @Param("assoId") Long assoId, @Param("sectionId") Long sectionId, Pageable pageable);

    @Query("SELECT a FROM Adhesion a WHERE a.userId = :userId AND a.section.sectionId = :sectionId")
    Optional<Adhesion> findByUserIdAndSectionId(@Param("userId") String keycloakUserId, @Param("sectionId") Long sectionId);

    @Query("SELECT a FROM Adhesion a WHERE a.userId = :userId AND a.association.assoId = :assoId")
    Optional<Adhesion> findByUserIdAndAsso(@Param("userId") String keycloakUserId, @Param("assoId") Long assoId);

    @Query("SELECT a FROM Adhesion a WHERE a.association.assoId = :assoId")
    List<Adhesion> getAdhesionsByAssoId(@Param("assoId") Long assoId);
}
