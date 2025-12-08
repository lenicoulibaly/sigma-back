package lenicorp.admin.workflowengine.execution.repo;

import lenicorp.admin.workflowengine.execution.model.WorkflowTransitionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowTransitionLogRepository extends JpaRepository<WorkflowTransitionLog, Long> {
    List<WorkflowTransitionLog> findByObjectTypeAndObjectIdOrderByOccurredAtDesc(String objectType, String objectId);
}
