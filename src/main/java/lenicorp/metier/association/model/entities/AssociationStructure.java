package lenicorp.metier.association.model.entities;

import jakarta.persistence.*;
import lenicorp.admin.security.audit.AuditableEntity;
import lenicorp.admin.structures.model.entities.Structure;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ASSOCIATION_STRUCTURE",
        uniqueConstraints = @UniqueConstraint(name = "UK_ASSO_STR", columnNames = {"ASSO_ID", "STR_ID"}))
@Audited
public class AssociationStructure extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ASSO_STR_ID_GEN")
    @SequenceGenerator(name = "ASSO_STR_ID_GEN", sequenceName = "ASSO_STR_ID_GEN", allocationSize = 10)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSO_ID", nullable = false)
    private Association association;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "STR_ID", nullable = false)
    private Structure structure;

    public AssociationStructure(Association association, Structure structure) {
        this.association = association;
        this.structure = structure;
    }
}
