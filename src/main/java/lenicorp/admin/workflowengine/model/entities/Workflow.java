package lenicorp.admin.workflowengine.model.entities;

import jakarta.persistence.*;
import lenicorp.admin.security.audit.AuditableEntity;
import lenicorp.admin.types.model.entities.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workflow")
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Workflow extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WF_ID_GEN")
    @SequenceGenerator(name = "WF_ID_GEN", sequenceName = "WF_ID_GEN", allocationSize = 10)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;     // EX: MARCHE_WF, SOUSCRIPTION_WF

    private String libelle;

    // Catégorie/type du workflow (optionnel)
    @ManyToOne @JoinColumn(name = "type_code")
    private Type type;

    // Type/table cible (ex: DEMANDE_ADHESION)
    @ManyToOne @JoinColumn(name = "target_table_name")
    private Type targetTableName;

    private Boolean active = true;

    // Etapes/statuts du workflow
    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("ordre ASC")
    private List<WorkflowStatus> statuses = new ArrayList<>();
}
