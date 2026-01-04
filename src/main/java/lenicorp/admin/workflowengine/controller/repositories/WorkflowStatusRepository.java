package lenicorp.admin.workflowengine.controller.repositories;

import java.util.List;
import lenicorp.admin.workflowengine.model.dtos.WorkflowStatusDTO;
import lenicorp.admin.workflowengine.model.entities.WorkflowStatus;
import lenicorp.admin.workflowengine.model.entities.WorkflowStatusGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkflowStatusRepository extends JpaRepository<WorkflowStatus, Long>
{

    List<WorkflowStatus> findAllByGroupsContains(WorkflowStatusGroup group);

    @Query("""
            SELECT new lenicorp.admin.workflowengine.model.dtos.WorkflowStatusDTO(
                ws.id,
                ws.status.code,
                ws.ordre,
                ws.regulatoryDurationValue,
                ws.regulatoryDurationUnit.code,
                ws.start,
                ws.end,
                ws.status.name,
                ws.color,
                ws.icon
            )
            FROM WorkflowStatus ws
            WHERE ws.workflow.id = ?1
            ORDER BY ws.ordre
            """)
    List<WorkflowStatusDTO> findByWorkflowId(Long workflowId);
    boolean existsByWorkflowIdAndStartTrue(Long workflowId);
    boolean existsByWorkflowIdAndEndTrue(Long workflowId);

    @Query(value = """
            SELECT new lenicorp.admin.workflowengine.model.dtos.WorkflowStatusDTO(
                ws.id,
                ws.status.code,
                ws.ordre,
                ws.regulatoryDurationValue,
                ws.regulatoryDurationUnit.code,
                ws.start,
                ws.end,
                ws.status.name,
                ws.color,
                ws.icon
            )
            FROM WorkflowStatus ws
            WHERE ws.workflow.id = :workflowId
            AND (
                 :key IS NULL
                 OR UPPER(FUNCTION('unaccent', COALESCE(ws.status.code, ''))) LIKE :key
                 OR UPPER(FUNCTION('unaccent', COALESCE(CAST(ws.ordre AS string), ''))) LIKE :key
                 OR UPPER(FUNCTION('unaccent', COALESCE(ws.regulatoryDurationUnit.code, ''))) LIKE :key
            )
            ORDER BY ws.ordre
            """,
            countQuery = """
            SELECT COUNT(ws) FROM WorkflowStatus ws
            WHERE ws.workflow.id = :workflowId
            AND (
                 :key IS NULL
                 OR UPPER(FUNCTION('unaccent', COALESCE(ws.status.code, ''))) LIKE :key
                 OR UPPER(FUNCTION('unaccent', COALESCE(CAST(ws.ordre AS string), ''))) LIKE :key
                 OR UPPER(FUNCTION('unaccent', COALESCE(ws.regulatoryDurationUnit.code, ''))) LIKE :key
            )
            """)
    Page<WorkflowStatusDTO> searchByWorkflow(@Param("workflowId") Long workflowId,
                                             @Param("key") String key,
                                             Pageable pageable);

    @Query("SELECT ws.status.code FROM WorkflowStatus ws WHERE ws.workflow.code = :workflowCode AND ws.start = true")
    String findStartStatusCodeByWorkflowCode(@Param("workflowCode") String workflowCode);
}
