package lenicorp.admin.security.audit;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

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
public abstract class AuditableEntity implements Serializable
{
    protected String actionName;
    protected String actionId;
    protected String connexionId;
    @Column(name = "created_at") @CreatedDate
    protected LocalDateTime createdAt;
    @Column(name = "created_by", length = 50) @CreatedBy
    protected String createdBy;
    @Column(name = "updated_at") @LastModifiedDate
    protected LocalDateTime updatedAt;
    @Column(name = "updated_by", length = 50) @LastModifiedBy
    protected String updatedBy;
    protected String ipAddress;
    protected String macAddress;
}