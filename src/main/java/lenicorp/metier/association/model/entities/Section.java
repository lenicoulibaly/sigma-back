package lenicorp.metier.association.model.entities;

import jakarta.persistence.*;
import lenicorp.admin.security.audit.AuditableEntity;
import lenicorp.admin.structures.model.entities.Structure;
import lombok.*;
import org.hibernate.envers.Audited;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Audited
@Entity
public class Section extends AuditableEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECTION_ID_GEN")
    @SequenceGenerator(name = "SECTION_ID_GEN", sequenceName = "SECTION_ID_GEN", allocationSize = 10)
    private Long sectionId;
    private String sectionName;
    private String situationGeo;
    private String sigle;
    private String email;
    private String tel;
    private String adresse;
    @ManyToOne @JoinColumn(name = "ASSOCIATION_ID")
    private Association association;

    @ManyToOne @JoinColumn(name = "STR_ID")
    private Structure strTutelle;

    public Section(Long sectionId) {
        this.sectionId = sectionId;
    }

    @Override
    public String toString() {
        return sectionId + "_" + sectionName + '_' + sigle;
    }
}
