package lenicorp.admin.workflowengine.controller.repositories;

import lenicorp.admin.workflowengine.model.dtos.TransitionDTO;
import lenicorp.admin.workflowengine.model.entities.Transition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransitionRepository extends JpaRepository<Transition, Long> {
    
    @Query("""
        SELECT new lenicorp.admin.workflowengine.model.dtos.TransitionDTO(
            t.transitionId, p.code, t.libelle, t.color, t.icon, t.ordre, 
            so.code, so.name, 
            sd.code, sd.name, 
            t.workflow.id, t.active,
            COALESCE(vc.commentRequired, false)
        )
        FROM Transition t
        LEFT JOIN t.privilege p
        LEFT JOIN t.statutOrigine so
        LEFT JOIN t.defaultStatutDestination sd
        LEFT JOIN t.validationConfig vc
        WHERE t.workflow.id = :workflowId
          AND t.active = true
          AND (
                :key IS NULL OR :key = '' OR
                LOWER(p.code) LIKE LOWER(CONCAT('%', :key, '%')) OR
                LOWER(COALESCE(t.libelle, '')) LIKE LOWER(CONCAT('%', :key, '%')) OR
                LOWER(COALESCE(so.code, '')) LIKE LOWER(CONCAT('%', :key, '%')) OR
                LOWER(COALESCE(sd.code, '')) LIKE LOWER(CONCAT('%', :key, '%'))
              )
        ORDER BY t.ordre ASC
    """)
    Page<TransitionDTO> searchByWorkflow(@Param("workflowId") Long workflowId,
                                         @Param("key") String key,
                                         Pageable pageable);

    @Query("""
        SELECT new lenicorp.admin.workflowengine.model.dtos.TransitionDTO(
            t.transitionId, p.code, t.libelle, t.color, t.icon, t.ordre, 
            so.code, so.name, 
            sd.code, sd.name, 
            t.workflow.id, t.active,
            COALESCE(vc.commentRequired, false)
        )
        FROM Transition t
        LEFT JOIN t.privilege p
        LEFT JOIN t.statutOrigine so
        LEFT JOIN t.defaultStatutDestination sd
        LEFT JOIN t.validationConfig vc
        WHERE t.workflow.code = :workflowCode 
          AND t.statutOrigine.code = :statusCode 
          AND t.active = true 
        ORDER BY t.ordre ASC
    """)
    List<TransitionDTO> findAvailableTransitions(@Param("workflowCode") String workflowCode, @Param("statusCode") String statusCode);

    @Query("""
        SELECT DISTINCT t.privilege.code
        FROM Transition t
        JOIN WorkflowStatus ws ON ws.status.code = t.statutOrigine.code
        JOIN ws.groups g
        WHERE g.id = :groupId
          AND t.privilege.code IS NOT NULL
          AND t.active = true
    """)
    List<String> findPrivilegeCodesByGroupId(@Param("groupId") Long groupId);
}