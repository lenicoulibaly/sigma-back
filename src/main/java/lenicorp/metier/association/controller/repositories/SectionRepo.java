package lenicorp.metier.association.controller.repositories;

import lenicorp.metier.association.model.dtos.ReadSectionDTO;
import lenicorp.metier.association.model.entities.Section;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionRepo extends JpaRepository<Section, Long>
{
    @Query(value = """
            SELECT NEW lenicorp.metier.association.model.dtos.ReadSectionDTO(
            s.sectionId, s.sectionName, s.situationGeo, s.sigle, a.assoId, a.assoName)
            FROM Section s
            LEFT JOIN s.association a
            LEFT JOIN s.strTutelle str
            WHERE
            (
                UPPER(FUNCTION('unaccent', COALESCE(s.sectionName, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(s.sigle, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(s.situationGeo, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(a.assoName, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(a.sigle, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(str.strName, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(str.strSigle, ''))) LIKE CONCAT('%', UPPER(:key), '%')
            )
            AND (:assoId IS NULL OR a.assoId = :assoId)
            AND (:strId IS NULL OR str.strId = :strId)
            """,
            countQuery = """
            SELECT COUNT(s)
            FROM Section s
            LEFT JOIN s.association a
            LEFT JOIN s.strTutelle str
            WHERE
            (
                UPPER(FUNCTION('unaccent', COALESCE(s.sectionName, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(s.sigle, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(s.situationGeo, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(a.assoName, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(a.sigle, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(str.strName, ''))) LIKE CONCAT('%', UPPER(:key), '%')
                OR UPPER(FUNCTION('unaccent', COALESCE(str.strSigle, ''))) LIKE CONCAT('%', UPPER(:key), '%')
            )
            AND (:assoId IS NULL OR a.assoId = :assoId)
            AND (:strId IS NULL OR str.strId = :strId)
            """)
    Page<ReadSectionDTO> searchSections(@Param("key") String key, @Param("assoId") Long assoId, @Param("strId") Long strId, Pageable pageable);

    @Query("""
            SELECT COUNT(s) > 0 FROM Section s 
            WHERE TRIM(UPPER(s.sectionName)) = TRIM(UPPER(:sectionName)) 
            AND s.association.assoId = :assoId
            """)
    boolean existsByNameAndAssoId(@Param("sectionName") String sectionName, @Param("assoId") Long assoId);

    @Query("""
            SELECT NEW lenicorp.metier.association.model.dtos.ReadSectionDTO(
            s.sectionId, s.sectionName, s.situationGeo, s.sigle, s.association.assoId, s.association.assoName)
            FROM Section s WHERE s.association.assoId = :assoId
            """)
    List<ReadSectionDTO> findbyAssoId(@Param("assoId") Long assoId);
}
