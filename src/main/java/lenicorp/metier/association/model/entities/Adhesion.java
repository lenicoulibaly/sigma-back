package lenicorp.metier.association.model.entities;

import jakarta.persistence.*;
import lenicorp.admin.security.audit.AuditableEntity;
import lenicorp.admin.types.model.entities.Type;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

@Getter
@Setter
@NoArgsConstructor
@Audited
@Entity
public class Adhesion extends AuditableEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADHESION_ID_GEN")
    @SequenceGenerator(name = "ADHESION_ID_GEN", sequenceName = "ADHESION_ID_GEN", allocationSize = 10)
    private Long adhesionId;
    @ManyToOne
    @JoinColumn(name = "ASSO_ID")
    private Association association;
    @ManyToOne
    @JoinColumn(name = "SECTION_ID")
    private Section section;

    @ManyToOne
    @JoinColumn(name = "STATUT_CODE")
    private Type statut;

    private boolean active;
    private String userId;

    public Adhesion(Long adhesionId)
    {
        this.adhesionId = adhesionId;
    }

    public Adhesion(Long adhesionId, Association association, Section section, boolean active, String userId)
    {
        this.adhesionId = adhesionId;
        this.association = association;
        this.section = section;
        this.active = active;
        this.userId = userId;
    }
}
