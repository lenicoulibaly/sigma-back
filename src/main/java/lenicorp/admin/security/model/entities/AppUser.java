package lenicorp.admin.security.model.entities;

import lenicorp.admin.security.audit.AuditableEntity;
import lenicorp.admin.structures.model.entities.Structure;
import lenicorp.admin.types.model.entities.Type;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "app_user")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Audited
public class AppUser extends AuditableEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APP_USER_ID_GEN")
    @SequenceGenerator(name = "APP_USER_ID_GEN", sequenceName = "APP_USER_ID_GEN")
    private Long userId;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = true)
    private String matricule;
    private String firstName;
    private String lastName;
    private String tel;
    @ManyToOne @JoinColumn(name = "grade_code") @NotAudited
    private Type grade;
    @Column() @NotAudited
    private String password;
    private LocalDate changePasswordDate;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean activated = false;
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean notBlocked = true;
    private LocalDateTime lastLogin;
    @ManyToOne @JoinColumn(name = "str_id")
    private Structure structure;
    public AppUser(Long userId)
    {
        this.userId = userId;
    }
}