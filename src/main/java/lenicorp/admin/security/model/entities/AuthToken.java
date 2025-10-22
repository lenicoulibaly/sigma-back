package lenicorp.admin.security.model.entities;

import lenicorp.admin.security.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder @Audited
public class AuthToken extends AuditableEntity
{
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TOKEN_ID_GEN")
    @SequenceGenerator(name = "TOKEN_ID_GEN", sequenceName = "TOKEN_ID_GEN", allocationSize = 10)
    private Long tokenId;
    @Column(unique = true)
    private String token;
    private LocalDateTime expirationDate;
    private LocalDateTime usageDate;
    private boolean alreadyUsed;
    private String password; // Mot de passe d'utilisation du token aléatoirement généré!
    private boolean emailSent;
    @ManyToOne
    private AppUser user;

    public AuthToken(Long tokenId)
    {
        this.tokenId = tokenId;
    }

    public AuthToken(String token, AppUser user)
    {
        this.token = token;
        this.emailSent = false;
        this.alreadyUsed = false;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.expirationDate = LocalDateTime.now().plusDays(1);
    }

    @Override
    public String toString() {
        return tokenId + "_"+ token ;
    }
}
