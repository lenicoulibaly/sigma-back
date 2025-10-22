package lenicorp.admin.security.model.views;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "v_role_privilege")
public class VRolePrivilege 
{
    @Id
    private Long rowNum;
    private String roleCode;
    private String roleName;
    private String roleDescription;
    private String privilegeCode;
    private String privilegeName;
    private String privilegeDescription;
    private String privilegeTypeCode;
    private String privilegeTypeName;
}