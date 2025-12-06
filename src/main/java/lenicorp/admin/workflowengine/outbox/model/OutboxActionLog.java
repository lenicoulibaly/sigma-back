package lenicorp.admin.workflowengine.outbox.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "outbox_action_log", indexes = {
        @Index(name = "ux_outbox_dedup_key", columnList = "dedup_key", unique = true)
})
@Data @NoArgsConstructor @AllArgsConstructor
public class OutboxActionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OUTBOX_ACT_LOG_ID_GEN")
    @SequenceGenerator(name = "OUTBOX_ACT_LOG_ID_GEN", sequenceName = "OUTBOX_ACT_LOG_ID_GEN", allocationSize = 10)
    private Long id;

    @Column(name = "dedup_key", nullable = false, unique = true, length = 512)
    private String dedupKey;

    @Column(nullable = false, length = 20)
    private String status; // RESERVED | SUCCESS | FAILED

    @Lob
    @Column(columnDefinition = "TEXT")
    private String lastError;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
