package lenicorp.admin.workflowengine.validation.model;

import jakarta.persistence.*;
import lenicorp.admin.types.model.entities.Type;
import lenicorp.admin.workflowengine.model.entities.Transition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transition_validation_cfg")
@Data @NoArgsConstructor @AllArgsConstructor
public class TransitionValidationConfig {
    @Id
    @Column(name = "transition_privilege_code", nullable = false, updatable = false)
    private String transitionPrivilegeCode; // PK and FK to Transition.privilegeCode

    @OneToOne(optional = false)
    @JoinColumn(name = "transition_privilege_code", referencedColumnName = "privilege_code", insertable = false, updatable = false)
    private Transition transition;

    @Column(nullable = false)
    private boolean commentRequired = false;

    @ManyToMany
    @JoinTable(name = "trans_valid_required_doc_type",
            joinColumns = @JoinColumn(name = "transition_privilege_code", referencedColumnName = "transition_privilege_code"),
            inverseJoinColumns = @JoinColumn(name = "type_code", referencedColumnName = "code"))
    private List<Type> requiredDocTypes = new ArrayList<>();
}
