package lenicorp.admin.workflowengine.controller.repositories;

import lenicorp.admin.workflowengine.model.dtos.WorkflowStatusGroupDTO;
import lenicorp.admin.workflowengine.model.entities.WorkflowStatusGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowStatusGroupRepository extends JpaRepository<WorkflowStatusGroup, Long> {
    Optional<WorkflowStatusGroup> findByCode(String code);
    boolean existsByCode(String code);
    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    @Query(value = """
            SELECT DISTINCT new lenicorp.admin.workflowengine.model.dtos.WorkflowStatusGroupDTO(
                wsg.id,
                wsg.code,
                wsg.name,
                wsg.description,
                wsg.color,
                wsg.ordre
            )
            FROM WorkflowStatusGroup wsg
            LEFT JOIN wsg.statuses ws
            WHERE (
                 :key IS NULL
                 OR UPPER(FUNCTION('unaccent', COALESCE(wsg.code, ''))) LIKE :key
                 OR UPPER(FUNCTION('unaccent', COALESCE(wsg.name, ''))) LIKE :key
                 OR UPPER(FUNCTION('unaccent', COALESCE(wsg.description, ''))) LIKE :key
            )
            AND (:workflowId IS NULL OR ws.workflow.id = :workflowId)
            ORDER BY wsg.ordre ASC, wsg.name ASC
            """,
            countQuery = """
            SELECT COUNT(DISTINCT wsg) FROM WorkflowStatusGroup wsg
            LEFT JOIN wsg.statuses ws
            WHERE (
                 :key IS NULL
                 OR UPPER(FUNCTION('unaccent', COALESCE(wsg.code, ''))) LIKE :key
                 OR UPPER(FUNCTION('unaccent', COALESCE(wsg.name, ''))) LIKE :key
                 OR UPPER(FUNCTION('unaccent', COALESCE(wsg.description, ''))) LIKE :key
            )
            AND (:workflowId IS NULL OR ws.workflow.id = :workflowId)
            """)
    Page<WorkflowStatusGroupDTO> searchAccessible(@Param("key") String key, @Param("workflowId") Long workflowId, Pageable pageable);

    @Query(value = """
            SELECT DISTINCT new lenicorp.admin.workflowengine.model.dtos.WorkflowStatusGroupDTO(
                wsg.id,
                wsg.code,
                wsg.name,
                wsg.description,
                wsg.color,
                wsg.ordre
            )
            FROM WorkflowStatusGroup wsg
            LEFT JOIN wsg.statuses ws
            WHERE (:workflowCode IS NULL OR ws.workflow.code = :workflowCode)
            ORDER BY wsg.ordre ASC, wsg.name ASC
            """)
    List<WorkflowStatusGroupDTO> getAccessibleWorkflowStatusGroupByWorkflowCode(@Param("workflowCode") String workflowCode);

    @Query("SELECT ws.status.code FROM WorkflowStatusGroup wsg JOIN wsg.statuses ws WHERE wsg.code = :groupCode")
    List<String> findStatusCodesByGroupCode(@Param("groupCode") String groupCode);
}
