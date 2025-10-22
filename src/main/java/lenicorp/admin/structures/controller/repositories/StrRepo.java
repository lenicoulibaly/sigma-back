package lenicorp.admin.structures.controller.repositories;

import lenicorp.admin.structures.model.dtos.ChangeAnchorDTO;
import lenicorp.admin.structures.model.dtos.CreateOrUpdateStrDTO;
import lenicorp.admin.structures.model.dtos.ReadStrDTO;
import lenicorp.admin.structures.model.entities.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StrRepo extends JpaRepository<Structure, Long>
{
    /**
     * Check if parent has compatible sous-type
     * @param strParentId The ID of the parent structure
     * @param childTypeCode The code of the child type
     * @return True if parent has compatible sous-type
     */
    @Query("SELECT COUNT(tm) > 0 FROM TypeMapping tm WHERE tm.parent.code = (SELECT s.strType.code FROM Structure s WHERE s.strId = :strParentId) AND UPPER(tm.child.code) = UPPER(:childTypeCode)")
    boolean parentHasCompatibleSousType(@Param("strParentId") Long strParentId, @Param("childTypeCode") String childTypeCode);

    /**
     * Check if sigle exists under same parent
     * @param sigle The sigle to check
     * @param parentId The ID of the parent structure
     * @param excludeStrId The ID of the structure to exclude
     * @return True if sigle exists under same parent
     */
    @Query("SELECT COUNT(s) > 0 FROM Structure s WHERE UPPER(s.strSigle) = UPPER(:sigle) AND (:parentId IS NULL AND s.strParent IS NULL OR s.strParent.strId = :parentId) AND (s.strId <> :excludeStrId OR :excludeStrId IS NULL)")
    boolean sigleExistsUnderSameParent(@Param("sigle") String sigle, @Param("parentId") Long parentId, @Param("excludeStrId") Long excludeStrId);

    /**
     * Check if structure exists by ID
     * @param strId The ID of the structure
     * @return True if structure exists
     */
    boolean existsById(Long strId);

    /**
     * Check if structure name exists under same parent
     * @param strName The name to check
     * @param parentId The ID of the parent structure
     * @param excludeStrId The ID of the structure to exclude
     * @return True if structure name exists under same parent
     */
    @Query("SELECT COUNT(s) > 0 FROM Structure s WHERE UPPER(s.strName) = UPPER(:strName) AND (:parentId IS NULL AND s.strParent IS NULL OR s.strParent.strId = :parentId) AND (s.strId <> :excludeStrId OR :excludeStrId IS NULL)")
    boolean strNameExistsUnderSameParent(@Param("strName") String strName, @Param("parentId") Long parentId, @Param("excludeStrId") Long excludeStrId);

    /**
     * Get possible parent structures for a child type
     * @param childTypeCode The code of the child type
     * @return List of possible parent structures
     */
    @Query("SELECT new lenicorp.admin.structures.model.dtos.ReadStrDTO(s.strId, s.strName, s.strTypeName, s.strSigle, s.strTypeCode, s.strTel, s.strAddress, s.situationGeo, s.parentId, s.parentName, s.parentSigle, s.strLevel, s.chaineSigles) FROM VStructure s WHERE s.strTypeCode IN (SELECT tm.parent.code FROM TypeMapping tm WHERE tm.child.code = :childTypeCode)")
    List<ReadStrDTO> getPossibleParentStructures(@Param("childTypeCode") String childTypeCode);

    /**
     * Get root structures
     * @return List of root structures
     */
    @Query("SELECT new lenicorp.admin.structures.model.dtos.ReadStrDTO(s.strId, s.strName, s.strTypeName, s.strSigle, s.strTypeCode, s.strTel, s.strAddress, s.situationGeo, s.parentId, s.parentName, s.parentSigle, s.strLevel, s.chaineSigles) FROM VStructure s WHERE s.parentId IS NULL")
    List<ReadStrDTO> getRootStructures();

    /**
     * Get update DTO for a structure
     * @param strId The ID of the structure
     * @return The update DTO
     */
    @Query("SELECT new lenicorp.admin.structures.model.dtos.CreateOrUpdateStrDTO(s.strId, s.strName, s.strSigle, s.strType.code, s.strParent.strId, s.strTel, s.strAddress, s.situationGeo) FROM Structure s WHERE s.strId = :strId")
    CreateOrUpdateStrDTO getUpdateDto(@Param("strId") Long strId);

    /**
     * Get change anchor DTO for a structure
     * @param strId The ID of the structure
     * @return The change anchor DTO
     */
    @Query("SELECT new lenicorp.admin.structures.model.dtos.ChangeAnchorDTO(s.strId, s.strType.code, s.strParent.strId, s.strName, s.strSigle, s.strTel, s.strAddress, s.situationGeo) FROM Structure s WHERE s.strId = :strId")
    ChangeAnchorDTO getChangeAnchorDto(@Param("strId") Long strId);
}
