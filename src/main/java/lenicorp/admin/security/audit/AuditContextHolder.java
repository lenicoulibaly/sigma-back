package lenicorp.admin.security.audit;

/**
 * Stocke des informations d'audit spécifiques à la requête courante
 * en utilisant un ThreadLocal.
 */
public final class AuditContextHolder
{
    private static final ThreadLocal<AuditContext> CONTEXT = ThreadLocal.withInitial(AuditContext::new);

    private AuditContextHolder() {}

    public static AuditContext get()
    {
        return CONTEXT.get();
    }

    public static void set(AuditContext context)
    {
        if (context == null) context = new AuditContext();
        CONTEXT.set(context);
    }

    public static void clear()
    {
        CONTEXT.remove();
    }
}
