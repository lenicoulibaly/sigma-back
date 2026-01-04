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

    @Query(value = """
            SELECT new lenicorp.admin.workflowengine.model.dtos.WorkflowStatusGroupDTO(
                wsg.id,
                wsg.code,
                wsg.name,
                wsg.description,
                wsg.color
            )
            FROM WorkflowStatusGroup wsg
            WHERE (
                 :key IS NULL
                 OR UPPER(FUNCTION('unaccent', COALESCE(wsg.code, ''))) LIKE :key
                 OR UPPER(FUNCTION('unaccent', COALESCE(wsg.name, ''))) LIKE :key
                 OR UPPER(FUNCTION('unaccent', COALESCE(wsg.description, ''))) LIKE :key
            )
            ORDER BY wsg.name
            """,
            countQuery = """
            SELECT COUNT(wsg) FROM WorkflowStatusGroup wsg
            WHERE (
                 :key IS NULL
                 OR UPPER(FUNCTION('unaccent', COALESCE(wsg.code, ''))) LIKE :key
                 OR UPPER(FUNCTION('unaccent', COALESCE(wsg.name, ''))) LIKE :key
                 OR UPPER(FUNCTION('unaccent', COALESCE(wsg.description, ''))) LIKE :key
            )
            """)
    Page<WorkflowStatusGroupDTO> search(@Param("key") String key, Pageable pageable);

    @Query("SELECT ws.status.code FROM WorkflowStatusGroup wsg JOIN wsg.statuses ws WHERE wsg.code = :groupCode")
    List<String> findStatusCodesByGroupCode(@Param("groupCode") String groupCode);
}
