package lenicorp.admin.workflowengine.outbox.exec;

public interface OutboxActionExecutor {
    String actionType();
    void execute(ActionContext ctx) throws Exception;
}
