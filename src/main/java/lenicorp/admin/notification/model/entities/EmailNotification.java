package lenicorp.admin.notification.model.entities;

import lenicorp.admin.security.audit.AuditableEntity;
import lenicorp.admin.security.model.entities.AppUser;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class EmailNotification extends AuditableEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTIF_ID_GEN")
    @SequenceGenerator(name = "NOTIF_ID_GEN", sequenceName = "NOTIF_ID_GEN", allocationSize = 10)
    private Long mailId;
    private String recipientName;
    private String email; //Recipient Email
    private String token;
    private LocalDateTime sendingDate;
    private boolean seen;
    private boolean sent;
    private String mailObject;
    @Column(length = 10000)
    private String mailMessage;

    private String senderUsername;

    private String systemMailSender; /* L'adresse mail utilisée pour envoyer le mail*/

    public EmailNotification(AppUser user, String mailObject, String token, String senderUsername)
    {
        this.recipientName = user.getEmail();
        this.email = user.getEmail();
        this.sendingDate = LocalDateTime.now();
        this.token = token;
        this.mailObject = mailObject;
        this.senderUsername = senderUsername;
    }

    @Override
    public String toString()
    {
        return mailId + "_" + recipientName + "_" + email + "_" + token;
    }
}
