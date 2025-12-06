package lenicorp.admin.workflowengine.outbox.service;

public interface DedupService {
    /**
     * Execute the runnable only once for the given dedupKey.
     * Returns true if executed in this call, false if already executed before.
     */
    boolean runOnce(String dedupKey, Runnable runnable);
}
