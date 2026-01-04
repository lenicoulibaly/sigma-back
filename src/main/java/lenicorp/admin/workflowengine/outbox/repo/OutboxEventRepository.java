package lenicorp.admin.workflowengine.outbox.repo;

import lenicorp.admin.workflowengine.outbox.model.entities.OutboxEvent;
import lenicorp.admin.workflowengine.outbox.model.enums.OutboxStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query("select e from OutboxEvent e where e.status in (?1, ?2) and (e.nextAttemptAt is null or e.nextAttemptAt <= ?3) order by e.createdAt asc")
    List<OutboxEvent> findReadyBatch(OutboxStatus s1, OutboxStatus s2, Instant now, Pageable pageable);
}
