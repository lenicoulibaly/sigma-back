package lenicorp.admin.workflowengine.model.entities;

import jakarta.persistence.*;
import lenicorp.admin.types.model.entities.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transition_validation_cfg")
@Data @NoArgsConstructor @AllArgsConstructor
public class TransitionValidationConfig
{
    @Id
    @Column(name = "transition_id")
    private Long transitionId;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "transition_id")
    @ToString.Exclude
    private Transition transition;

    @Column(nullable = false)
    private boolean commentRequired = false;

    @ManyToMany
    @JoinTable(name = "transition_required_doc_type",
            joinColumns = @JoinColumn(name = "transition_id", referencedColumnName = "transition_id"),
            inverseJoinColumns = @JoinColumn(name = "type_code", referencedColumnName = "code"))
    private List<Type> requiredDocTypes = new ArrayList<>();
}
