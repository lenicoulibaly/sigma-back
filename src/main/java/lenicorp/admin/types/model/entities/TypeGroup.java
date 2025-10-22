package lenicorp.admin.types.model.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

@Entity
@Table(name = "type_group")
@NoArgsConstructor @AllArgsConstructor @Audited
public class TypeGroup
{
    @Id
    public String groupCode;
    public String name;
    public TypeGroup(String groupCode) {
        this.groupCode = groupCode;
    }
}
