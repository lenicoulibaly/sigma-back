package lenicorp.admin.types.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "type_mapping", uniqueConstraints = {@UniqueConstraint(columnNames = {"parent_code", "child_code"})})
@NoArgsConstructor @AllArgsConstructor
public class TypeMapping
{
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE) @SequenceGenerator(name = "TYPE_MAPPING_ID_GEN", sequenceName = "TYPE_MAPPING_ID_GEN", initialValue = 1)
    public Long mappingId;
    @ManyToOne(optional = false) @JoinColumn(name = "parent_code", nullable = false)
    public Type parent;
    @ManyToOne(optional = false) @JoinColumn(name = "child_code", nullable = false)
    public Type child;
}
