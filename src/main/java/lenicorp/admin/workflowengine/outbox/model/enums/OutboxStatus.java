package lenicorp.admin.workflowengine.outbox.model.enums;

public enum OutboxStatus {
    NEW,
    PROCESSING,
    RETRY,
    SENT,
    DEAD
}
