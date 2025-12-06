package lenicorp.admin.workflowengine.engine.registry;

import lenicorp.admin.workflowengine.model.entities.Workflow;

public interface WorkflowRegistry {
    Workflow resolveByCode(String workflowCode);

    /**
     * Resolve a workflow for a given object type (target table/type code). If multiple exist, may throw unless a
     * selector is provided elsewhere. For this MVP, returns the first active workflow if only one exists.
     */
    Workflow resolveByTargetTypeCode(String targetTypeCode);
}
