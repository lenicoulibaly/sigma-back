package lenicorp.admin.workflowengine.model.entities;

import jakarta.persistence.*;
import lenicorp.admin.security.audit.AuditableEntity;
import lenicorp.admin.types.model.entities.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transition_rule")
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TransitionRule extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRANSITION_RULE_ID_GEN")
    @SequenceGenerator(name = "TRANSITION_RULE_ID_GEN", sequenceName = "TRANSITION_RULE_ID_GEN", allocationSize = 10)
    private Long id;

    private Integer ordre = 0;

    @ManyToOne
    @JoinColumn(name = "transition_id")
    private Transition transition;

    /**
     * Statut cible si la règle matche
     */
    @ManyToOne
    @JoinColumn(name = "sta_dest_code")
    private Type statutDestination;

    /**
     * JSON des conditions
     */
    @Column(columnDefinition = "TEXT")
    private String ruleJson;

    private Boolean active = true;
}
