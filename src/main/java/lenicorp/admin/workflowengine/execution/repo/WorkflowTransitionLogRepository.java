package lenicorp.admin.workflowengine.execution.repo;

import lenicorp.admin.workflowengine.execution.model.WorkflowTransitionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkflowTransitionLogRepository extends JpaRepository<WorkflowTransitionLog, Long> {
    @Query("SELECT l FROM WorkflowTransitionLog l " +
            "WHERE l.objectType = :objectType AND l.objectId = :objectId " +
            "ORDER BY l.createdAt DESC")
    List<WorkflowTransitionLog> findByObjectTypeAndObjectId(@Param("objectType") String objectType, @Param("objectId") String objectId);

    @Query("SELECT l FROM WorkflowTransitionLog l " +
            "WHERE l.objectType = :objectType AND l.objectId = :objectId " +
            "AND (:key IS NULL OR UPPER(coalesce(l.comment, '')) LIKE UPPER(CONCAT('%', :key, '%'))) " +
            "AND (:transitionIds IS NULL OR l.transitionId IN :transitionIds) " +
            "ORDER BY l.createdAt DESC")
    Page<WorkflowTransitionLog> searchHistory(@Param("objectType") String objectType,
                                              @Param("objectId") String objectId,
                                              @Param("key") String key,
                                              @Param("transitionIds") List<Long> transitionIds,
                                              Pageable pageable);
}
