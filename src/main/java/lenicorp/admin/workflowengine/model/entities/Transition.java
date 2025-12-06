package lenicorp.admin.workflowengine.model.entities;

import jakarta.persistence.*;
import lenicorp.admin.types.model.entities.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "transition")
@Data @NoArgsConstructor @AllArgsConstructor
public class Transition {
    @Id
    @Column(name = "privilege_code")
    private String privilegeCode; // sert aussi d'autorisation requise

    @Column(nullable = false)
    private String code;        // APPROUVER, REJETER

    private String libelle;     // "Approuver la demande"

    @Column(nullable = false)
    private Integer ordre = 0;

    @ManyToOne @JoinColumn(name = "sta_orig_code")
    private Type statutOrigine;

    @ManyToOne @JoinColumn(name = "default_sta_dest_code")
    private Type defaultStatutDestination;

    @ManyToOne @JoinColumn(name = "workflow_id")
    private Workflow workflow;

    @OneToMany(mappedBy = "transition", cascade = CascadeType.ALL)
    @OrderBy("ordre ASC")
    private List<TransitionRule> rules;

    private Boolean active = true;
}
