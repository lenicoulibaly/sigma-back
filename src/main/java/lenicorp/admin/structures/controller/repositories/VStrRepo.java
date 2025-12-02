package lenicorp.admin.structures.controller.repositories;

import lenicorp.admin.structures.model.dtos.ReadStrDTO;
import lenicorp.admin.structures.model.entities.VStructure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VStrRepo extends JpaRepository<VStructure, Long>
{
    /**
     * Find all descendants of a structure
     * @param strId The ID of the parent structure
     * @return List of descendant structures
     */
    @Query("SELECT vs FROM VStructure vs WHERE vs.chaineSigles LIKE CONCAT(:parentChaineSigles, '/%') OR vs.strId = :strId ORDER BY vs.chaineSigles")
    List<VStructure> findDescendantsByChaineSigles(@Param("parentChaineSigles") String parentChaineSigles, @Param("strId") Long strId);


    /**
     * Get the chaineSigles for a structure
     * @param strId The ID of the structure
     * @return The chaineSigles
     */
    @Query("SELECT vs.chaineSigles FROM VStructure vs WHERE vs.strId = :strId")
    String getChaineSigles(@Param("strId") Long strId);

    @Query(value = """
         SELECT new lenicorp.admin.structures.model.dtos.ReadStrDTO(
             vs.strId, vs.strName, vs.strTypeName, vs.strSigle, vs.strTypeCode
             , vs.strTel, vs.strAddress, vs.situationGeo, vs.parentId, vs.parentName
             , vs.parentSigle, vs.strLevel, vs.chaineSigles) 
         FROM VStructure vs 
         WHERE locate(:key, vs.searchText) >0
            and vs.strTypeCode = coalesce(:typeCode, vs.strTypeCode) 
            and locate(function('getstrchainesigles', coalesce(:parentId, vs.parentId)), function('getstrchainesigles', vs.strId)) = 1
    """)
    List<ReadStrDTO> searchStrList(@Param("key") String key, @Param("parentId") Long parentId, @Param("typeCode") String typeCode);


    @Query(value = """
         SELECT new lenicorp.admin.structures.model.dtos.ReadStrDTO(
             vs.strId, vs.strName, vs.strTypeName, vs.strSigle, vs.strTypeCode
             , vs.strTel, vs.strAddress, vs.situationGeo, vs.parentId, vs.parentName
             , vs.parentSigle, vs.strLevel, vs.chaineSigles) 
         FROM VStructure vs 
         WHERE locate(:key, vs.searchText) >0
            and vs.strTypeCode = coalesce(:typeCode, vs.strTypeCode) 
            and locate(function('getstrchainesigles', coalesce(:parentId, vs.parentId)), function('getstrchainesigles', vs.strId)) = 1
    """,
    countQuery = """
        SELECT count(vs.strId) FROM VStructure vs 
        WHERE locate(:key, vs.searchText) >0
            and vs.strTypeCode = coalesce(:typeCode, vs.strTypeCode)
            and locate(function('getstrchainesigles', coalesce(:parentId, vs.parentId)), function('getstrchainesigles', vs.strId)) = 1 
    """)
    Page<ReadStrDTO> search(@Param("key") String key, @Param("parentId") Long parentId, @Param("typeCode") String typeCode, Pageable pageable);

}
