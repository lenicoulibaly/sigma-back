package lenicorp.admin.security.model.views;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "v_privilege")
public class VPrivilege
{
    @Id
    private String code;
    private String name;
    private String typeCode;
    @Size(max = 255) @Column(name = "description")
    private String description;
    @Size(max = 255) @Column(name = "privilege_type_code")
    private String privilegeTypeCode;

    @Size(max = 100)
    @Column(name = "privilege_type_name", length = 100)
    private String privilegeTypeName;

}