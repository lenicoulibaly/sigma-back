package lenicorp.admin.security.audit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Custom revision entity for Hibernate Envers.
 * This entity stores information about each revision, including the user who made the change.
 */
@Entity
@Table(schema = "audit", name = "REVINFO")
@RevisionEntity(EnversRevisionListener.class)
@Getter
@Setter
public class CustomRevisionEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "revision_seq")
    @SequenceGenerator(name = "revision_seq", sequenceName = "audit.REVISION_SEQ", initialValue = 1, allocationSize = 1)
    @RevisionNumber
    private Long id;

    @RevisionTimestamp
    private long timestamp;

    @Column(name = "username", nullable = true)
    private String username;

    @ElementCollection
    @JoinTable(schema = "audit", name = "REVCHANGES", joinColumns = @JoinColumn(name = "REV"))
    @Column(name = "ENTITYNAME")
    private Set<String> modifiedEntityNames = new HashSet<>();

    public Date getRevisionDate() {
        return new Date(timestamp);
    }
}
