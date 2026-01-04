package lenicorp.admin.workflowengine.model.entities;

import jakarta.persistence.*;
import lenicorp.admin.security.model.entities.AppAuthority;
import lenicorp.admin.types.model.entities.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "transition")
@Data @NoArgsConstructor @AllArgsConstructor
public class Transition {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TRANSITION_ID_GEN")
    @SequenceGenerator(name = "TRANSITION_ID_GEN", sequenceName = "TRANSITION_ID_GEN", allocationSize = 1)
    private Long transitionId;

    @ManyToOne
    @JoinColumn(name = "privilege_code")
    private AppAuthority privilege; // sert aussi d'autorisation requise

    private String libelle;     // "Approuver la demande"

    private String color;

    private String icon;

    @Column(nullable = false)
    private Integer ordre = 0;

    @ManyToOne @JoinColumn(name = "sta_orig_code")
    private Type statutOrigine;

    @ManyToOne @JoinColumn(name = "default_sta_dest_code")
    private Type defaultStatutDestination;

    @ManyToOne @JoinColumn(name = "workflow_id")
    @ToString.Exclude
    private Workflow workflow;

    @OneToMany(mappedBy = "transition", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<TransitionRule> rules;

    @OneToOne(mappedBy = "transition", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private TransitionValidationConfig validationConfig;

    @OneToMany(mappedBy = "transition", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<TransitionSideEffect> sideEffects;

    private Boolean active = true;
}
