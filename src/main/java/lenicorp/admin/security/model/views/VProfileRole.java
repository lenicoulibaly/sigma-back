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
@Table(name = "v_profile_role")
public class VProfileRole 
{
    @Id
    private Long rowNum;
    private String profileCode;
    private String profileName;
    private String profileDescription;
    private String roleCode;
    private String roleName;
    private String roleDescription;
}