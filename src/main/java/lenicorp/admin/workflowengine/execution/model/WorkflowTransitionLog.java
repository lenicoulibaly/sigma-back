package lenicorp.admin.workflowengine.execution.model;

import jakarta.persistence.*;
import lenicorp.admin.security.audit.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workflow_transition_log", indexes = {
        @Index(name = "ix_wf_log_object", columnList = "objectType, objectId")
})
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class WorkflowTransitionLog extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WF_TRANS_LOG_ID_GEN")
    @SequenceGenerator(name = "WF_TRANS_LOG_ID_GEN", sequenceName = "WF_TRANS_LOG_ID_GEN", allocationSize = 10)
    private Long id;

    @Column(nullable = false)
    private String workflowCode;

    private Long transitionId;

    private String transitionPrivilegeCode;

    @Column(nullable = false)
    private String objectType;

    @Column(nullable = false)
    private String objectId;

    private String fromStatus;

    @Column(nullable = false)
    private String toStatus;

    private String actorUsername;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(columnDefinition = "TEXT")
    private String contextJson;


    @OneToMany(mappedBy = "log", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkflowTransitionAttachment> attachments = new ArrayList<>();
}
