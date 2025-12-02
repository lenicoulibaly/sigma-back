package lenicorp.admin.security.audit;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

/**
 * Listener JPA pour propager les infos (connexionId, ipAddress, macAddress)
 * du contexte d'audit vers les entités héritant de {@link AuditableEntity}.
 */
public class CustomAuditableEntityListener
{
    @PrePersist
    public void prePersist(Object entity)
    {
        if (!(entity instanceof AuditableEntity auditable)) return;

        AuditContext ctx = AuditContextHolder.get();
        if (ctx == null) return;

        if (auditable.getConnexionId() == null) {
            auditable.setConnexionId(ctx.getConnexionId());
        }
        if (auditable.getIpAddress() == null) {
            auditable.setIpAddress(ctx.getIpAddress());
        }
        if (auditable.getMacAddress() == null) {
            auditable.setMacAddress(ctx.getMacAddress());
        }
    }

    @PreUpdate
    public void preUpdate(Object entity)
    {
        if (!(entity instanceof AuditableEntity auditable)) return;

        AuditContext ctx = AuditContextHolder.get();
        if (ctx == null) return;

        // On n'écrase pas un connexionId existant, mais on le pose si absent
        if (auditable.getConnexionId() == null) {
            auditable.setConnexionId(ctx.getConnexionId());
        }
        // On met à jour IP/MAC si disponibles
        if (ctx.getIpAddress() != null) {
            auditable.setIpAddress(ctx.getIpAddress());
        }
        if (ctx.getMacAddress() != null) {
            auditable.setMacAddress(ctx.getMacAddress());
        }
    }
}
