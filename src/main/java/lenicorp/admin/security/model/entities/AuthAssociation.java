package lenicorp.admin.security.model.entities;

import lenicorp.admin.structures.model.entities.Structure;
import lenicorp.admin.types.model.entities.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "auth_association",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "profile_code", "str_id"}),
                @UniqueConstraint(columnNames = {"profile_code", "role_code"}),
                @UniqueConstraint(columnNames = {"role_code", "privilege_code"})
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthAssociation
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String libelle;
    @ManyToOne
    @JoinColumn(name = "type_code")
    private Type type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_code")
    private AppAuthority profile;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_code")
    private AppAuthority role;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "privilege_code")
    private AppAuthority privilege;
    @ManyToOne
    @JoinColumn(name = "ass_status_code")
    private Type assStatus;
    @ManyToOne
    @JoinColumn(name = "str_id")
    private Structure structure;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToOne @JoinColumn(name = "user_profile_ass_type_code")
    private Type userProfileAssType; //Titulaire, Intérimaire
    private LocalDate startingDate;
    private LocalDate endingDate;
    @Column(name = "asso_id")
    private Long assoId;
    @Column(name = "section_id")
    private Long sectionId;

    public static AuthAssociation createRolPrvAss(String roleCode, String privilegeCode)
    {
        AuthAssociation authAssociation = new AuthAssociation();
        authAssociation.setRole(new AppAuthority(roleCode));
        authAssociation.setPrivilege(new AppAuthority(privilegeCode));
        authAssociation.setType(new Type("ROL_PRV")) ;
        return authAssociation;
    }

    public static AuthAssociation createPrflRolAss(String profileCode, String roleCode)
    {
        AuthAssociation authAssociation = new AuthAssociation();
        authAssociation.setProfile(new AppAuthority(profileCode));
        authAssociation.setRole(new AppAuthority(roleCode));
        authAssociation.setType(new Type("PRFL_ROL")) ;
        return authAssociation;
    }

    public static AuthAssociation createUsrPrflAss(Long userId, String profileCode, Long strId)
    {
        AuthAssociation authAssociation = new AuthAssociation();
        authAssociation.setUser(new AppUser(userId));
        authAssociation.setProfile(new AppAuthority(profileCode));
        authAssociation.setStructure(new Structure(strId));
        authAssociation.setType(new Type("PRFL_ROL"));
        return authAssociation;
    }

    public static AuthAssociation createUsrPrflAss(Long userId, String profileCode, Long strId, Long associationId, Long sectionId)
    {
        AuthAssociation authAssociation = createUsrPrflAss(userId, profileCode, strId);
        authAssociation.setAssoId(associationId);
        authAssociation.setSectionId(sectionId);
        return authAssociation;
    }
}