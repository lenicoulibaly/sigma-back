package lenicorp.admin.workflowengine.outbox.repo;

import lenicorp.admin.workflowengine.outbox.model.entities.OutboxActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OutboxActionLogRepository extends JpaRepository<OutboxActionLog, Long> {
    Optional<OutboxActionLog> findByDedupKey(String dedupKey);
}
