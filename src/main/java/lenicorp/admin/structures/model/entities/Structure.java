package lenicorp.admin.structures.model.entities;

import lenicorp.admin.security.audit.AuditableEntity;
import lenicorp.admin.types.model.entities.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "structure")
@Audited
public class Structure extends AuditableEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "STRUCTURE_ID_GEN")
    @SequenceGenerator(name = "STRUCTURE_ID_GEN", sequenceName = "STRUCTURE_ID_GEN", initialValue = 1)
    private Long strId;
    private String strName;
    private String strSigle;
    @ManyToOne
    @JoinColumn(name = "PARENT_ID")
    private Structure strParent;
    @ManyToOne
    @JoinColumn(name = "STR_TYPE_CODE") @NotAudited
    private Type strType;

    private String strTel;
    private String strAddress;
    private String situationGeo;

    @Transient
    private List<Structure> strChildren;

    public Structure(Long strId)
    {
        this.strId = strId;
    }

    @Override
    public String toString()
    {
        return this.strName + " (" + this.strSigle + ")";
    }
}
