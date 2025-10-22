package lenicorp.admin.types.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Table(name = "type")
@NoArgsConstructor @AllArgsConstructor @Audited
public class Type
{
    @Id
    public String code;
    @Column(length = 100, nullable = false)
    public String name;
    @Column(columnDefinition = "INT DEFAULT 0")
    public Long ordre;
    @ManyToOne @JoinColumn(name = "group_code") @NotAudited
    public TypeGroup typeGroup;
    @Column(length = 255)
    public String description;

    public Type(String code)
    {
        this.code = code;
    }
}