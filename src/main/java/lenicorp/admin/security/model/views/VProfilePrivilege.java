package lenicorp.admin.security.model.views;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

/**
 * Mapping for DB view
 */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "v_profile_privilege")
public class VProfilePrivilege
{
    @Id
    @Column(name = "row_num")
    private Long rowNum;

    @Size(max = 255)
    @Column(name = "profile_code")
    private String profileCode;

    @Size(max = 255)
    @Column(name = "profile_description")
    private String profileDescription;

    @Size(max = 255)
    @Column(name = "profile_name")
    private String profileName;

    @Size(max = 255)
    @Column(name = "privilege_code")
    private String privilegeCode;

    @Size(max = 255)
    @Column(name = "privilege_description")
    private String privilegeDescription;

    @Size(max = 255)
    @Column(name = "privilege_name")
    private String privilegeName;

    private String privilegeTypeCode;
    private String privilegeTypeName;
}