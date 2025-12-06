package lenicorp.admin.workflowengine.outbox.model;

public enum OutboxStatus {
    NEW,
    PROCESSING,
    RETRY,
    SENT,
    DEAD
}
