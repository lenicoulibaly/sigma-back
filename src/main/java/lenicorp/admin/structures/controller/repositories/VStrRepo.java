package lenicorp.admin.structures.controller.repositories;

import lenicorp.admin.structures.model.entities.VStructure;
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

}
