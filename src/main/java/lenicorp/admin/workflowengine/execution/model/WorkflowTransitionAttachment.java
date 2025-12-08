package lenicorp.admin.workflowengine.execution.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "workflow_transition_attachment", indexes = {
        @Index(name = "ix_wf_trans_att_log", columnList = "log_id")
})
@Data @NoArgsConstructor @AllArgsConstructor
public class WorkflowTransitionAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WF_TRANS_ATT_ID_GEN")
    @SequenceGenerator(name = "WF_TRANS_ATT_ID_GEN", sequenceName = "WF_TRANS_ATT_ID_GEN", allocationSize = 10)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "log_id")
    private WorkflowTransitionLog log;

    @Column(nullable = false)
    private Long documentId; // reference to archive Document.docId

    private String name;
    private String contentType;
    private Long size;
}
