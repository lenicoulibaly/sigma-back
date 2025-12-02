package lenicorp.metier.association.model.entities;

import jakarta.persistence.*;
import lenicorp.admin.security.audit.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Audited
@Entity
public class Association extends AuditableEntity
{
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ASSO_ID_GEN")
    @SequenceGenerator(name = "ASSO_ID_GEN", sequenceName = "ASSO_ID_GEN", allocationSize = 10)
    private Long assoId;
    private String assoName;
    private String situationGeo;
    private String sigle;
    private BigDecimal droitAdhesion;
    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean masculin;
    private String email;
    private String tel;
    private String adresse;
    @Column(length = 10000)
    private String conditionsAdhesion;

    public Association(Long assoId) {
        this.assoId = assoId;
    }

    @Override
    public String toString() {
        return assoId + "_" + assoName + "_" + sigle;
    }
}
