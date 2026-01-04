package lenicorp.admin.workflowengine.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transition_side_effects")
@Data @NoArgsConstructor @AllArgsConstructor
public class TransitionSideEffect {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRANS_SE_ID_GEN")
    @SequenceGenerator(name = "TRANS_SE_ID_GEN", sequenceName = "TRANS_SE_ID_GEN", allocationSize = 10)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "transition_id")
    private Transition transition;

    @Column()
    private String name;

    @Column(name = "action_type", nullable = false)
    private String actionType; // RUN_BEAN_METHOD, SEND_EMAIL, etc.

    @Column(name = "action_config", columnDefinition = "TEXT")
    private String actionConfig; // JSON
    // configuration

    @Column(nullable = false)
    private Integer ordre = 0;
}
