package lenicorp.admin.workflowengine.outbox.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
@Data @NoArgsConstructor @AllArgsConstructor
public class OutboxEvent {
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(nullable = false, length = 100)
    private String eventType; // e.g., TransitionApplied

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload; // JSON serialized event with actions

    @Lob
    @Column(columnDefinition = "TEXT")
    private String headers; // optional JSON headers

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status = OutboxStatus.NEW;

    @Column(nullable = false)
    private int attempts = 0;

    private Instant nextAttemptAt; // null means ready now

    @Lob
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public void markProcessing() {
        this.status = OutboxStatus.PROCESSING;
    }

    public void markSent() {
        this.status = OutboxStatus.SENT;
        this.errorMessage = null;
    }

    public void scheduleRetry(Instant when, String error, int newAttempts) {
        this.status = OutboxStatus.RETRY;
        this.nextAttemptAt = when;
        this.attempts = newAttempts;
        this.errorMessage = error;
    }

    public void markDead(String error) {
        this.status = OutboxStatus.DEAD;
        this.errorMessage = error;
    }
}
