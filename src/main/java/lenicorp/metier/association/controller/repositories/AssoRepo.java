package lenicorp.metier.association.controller.repositories;

import lenicorp.metier.association.model.dtos.ReadAssociationDTO;
import lenicorp.metier.association.model.entities.Association;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssoRepo extends JpaRepository<Association, Long>
{
    @Query(value = """
            SELECT NEW lenicorp.metier.association.model.dtos.ReadAssociationDTO(
                a.assoId, a.assoName, a.situationGeo, a.sigle, a.droitAdhesion)
            FROM Association a 
            WHERE 
            (
                UPPER(FUNCTION('unaccent', COALESCE(a.assoName, ''))) LIKE :key
                OR UPPER(FUNCTION('unaccent', COALESCE(a.situationGeo, ''))) LIKE :key
                OR UPPER(FUNCTION('unaccent', COALESCE(a.sigle, ''))) LIKE :key
            )
            """,
            countQuery = """
            SELECT COUNT(a)
            FROM Association a 
            WHERE 
            (
                UPPER(FUNCTION('unaccent', COALESCE(a.assoName, ''))) LIKE :key
                OR UPPER(FUNCTION('unaccent', COALESCE(a.situationGeo, ''))) LIKE :key
                OR UPPER(FUNCTION('unaccent', COALESCE(a.sigle, ''))) LIKE :key
            )
            """)
    Page<ReadAssociationDTO> searchAssociations(@Param("key") String key, Pageable pageable);

    @Query(value = """
            SELECT NEW lenicorp.metier.association.model.dtos.ReadAssociationDTO(
                a.assoId, a.assoName, a.situationGeo, a.sigle, a.droitAdhesion)
            FROM Association a 
            WHERE 
            (
                UPPER(FUNCTION('unaccent', COALESCE(a.assoName, ''))) LIKE :key
                OR UPPER(FUNCTION('unaccent', COALESCE(a.situationGeo, ''))) LIKE :key
                OR UPPER(FUNCTION('unaccent', COALESCE(a.sigle, ''))) LIKE :key
            )
            """)
    List<ReadAssociationDTO> searchAssociationsList(@Param("key") String key);

    @Query("""
            SELECT s.strSigle FROM Structure s WHERE s.strId = :strId
            """)
    String getStrCode(@Param("strId") Long strId);

    @Query("""
            SELECT COUNT(a) > 0 FROM Association a 
            WHERE TRIM(UPPER(a.assoName)) = TRIM(UPPER(:assoName))
            """)
    boolean existsByName(@Param("assoName") String assoName);

    @Query("""
            SELECT COUNT(a) > 0 FROM Association a 
            WHERE TRIM(UPPER(a.assoName)) = TRIM(UPPER(:assoName)) 
            AND a.assoId <> :assoId
            """)
    boolean existsByName(@Param("assoName") String assoName, @Param("assoId") Long assoId);

    @Query("""
            SELECT NEW lenicorp.metier.association.model.dtos.ReadAssociationDTO(
                a.assoId, a.assoName, a.situationGeo, a.sigle, a.droitAdhesion, a.email, a.tel, a.adresse, a.conditionsAdhesion)
            FROM Association a WHERE a.assoId = :assoId
            """)
    ReadAssociationDTO findReadAssoDtoById(@Param("assoId") Long assoId);

    @Query("""
            SELECT a.sigle FROM Association a WHERE a.assoId = :assoId
            """)
    String getSigleByAssoId(@Param("assoId") Long assoId);
}