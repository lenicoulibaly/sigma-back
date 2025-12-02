package lenicorp.admin.security.audit;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base class for auditable entities.
 * This class provides common auditing fields that are inherited by entities that extend it.
 */
@MappedSuperclass
@Getter
@Setter
@Audited
@EntityListeners({AuditingEntityListener.class, CustomAuditableEntityListener.class})
public abstract class AuditableEntity implements Serializable
{
    protected String connexionId;
    @Column(name = "created_at", updatable = false) @CreatedDate
    protected LocalDateTime createdAt;
    @Column(name = "created_by", length = 50, updatable = false) @CreatedBy
    protected String createdBy;
    @Column(name = "updated_at") @LastModifiedDate
    protected LocalDateTime updatedAt;
    @Column(name = "updated_by", length = 50) @LastModifiedBy
    protected String updatedBy;
    protected String ipAddress;
    protected String macAddress;
}