package lenicorp.admin.workflowengine.model.entities;

import jakarta.persistence.*;
import lenicorp.admin.types.model.entities.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "workflow_status")
@Data @NoArgsConstructor @AllArgsConstructor
public class WorkflowStatus
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WF_STA_ID_GEN")
    @SequenceGenerator(name = "WF_STA_ID_GEN", sequenceName = "WF_STA_ID_GEN", allocationSize = 10)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "workflow_id")
    @ToString.Exclude
    private Workflow workflow;

    // Statut issu du référentiel Type
    @ManyToOne(optional = false)
    @JoinColumn(name = "status_code")
    private Type status;

    // Position de l’étape dans le workflow
    @Column(nullable = false)
    private Integer ordre;

    // Valeur numérique de la durée réglementaire pour cette étape
    @Column(name = "regulatory_duration_value")
    private Integer regulatoryDurationValue;

    // Unité de la durée réglementaire (liée à Type) ex: HEURE, JOUR, SEMAINE
    @ManyToOne
    @JoinColumn(name = "regulatory_duration_unit_code")
    private Type regulatoryDurationUnit;

    @Column(name = "is_start")
    private Boolean start = false;

    @Column(name = "is_end")
    private Boolean end = false;

    private String color;

    private String icon;

    @ManyToMany
    @JoinTable(
            name = "workflow_status_groups_rel",
            joinColumns = @JoinColumn(name = "status_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private List<WorkflowStatusGroup> groups;
}
