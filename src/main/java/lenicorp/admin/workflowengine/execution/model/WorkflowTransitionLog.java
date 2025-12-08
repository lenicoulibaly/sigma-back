package lenicorp.admin.workflowengine.execution.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workflow_transition_log", indexes = {
        @Index(name = "ix_wf_log_object", columnList = "objectType, objectId")
})
@Data @NoArgsConstructor @AllArgsConstructor
public class WorkflowTransitionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WF_TRANS_LOG_ID_GEN")
    @SequenceGenerator(name = "WF_TRANS_LOG_ID_GEN", sequenceName = "WF_TRANS_LOG_ID_GEN", allocationSize = 10)
    private Long id;

    @Column(nullable = false)
    private String workflowCode;

    @Column(nullable = false)
    private String transitionCode;

    @Column(nullable = false)
    private String objectType;

    @Column(nullable = false)
    private String objectId;

    @Column(nullable = false)
    private String fromStatus;

    @Column(nullable = false)
    private String toStatus;

    private String actorUsername;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String comment;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String contextJson;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant occurredAt;

    @OneToMany(mappedBy = "log", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowTransitionAttachment> attachments = new ArrayList<>();
}
