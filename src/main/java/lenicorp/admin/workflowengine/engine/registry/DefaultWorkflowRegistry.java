package lenicorp.admin.workflowengine.engine.registry;

import lenicorp.admin.workflowengine.model.entities.Workflow;
import lenicorp.admin.workflowengine.model.repositories.WorkflowRepository;
import org.springframework.stereotype.Component;

@Component
public class DefaultWorkflowRegistry implements WorkflowRegistry {
    private final WorkflowRepository workflowRepository;

    public DefaultWorkflowRegistry(WorkflowRepository workflowRepository) {
        this.workflowRepository = workflowRepository;
    }

    @Override
    public Workflow resolveByCode(String workflowCode) {
        if (workflowCode == null) return null;
        return workflowRepository.findByCodeAndActiveTrue(workflowCode)
                .orElseThrow(() -> new IllegalArgumentException("Workflow not found or inactive: " + workflowCode));
    }

    @Override
    public Workflow resolveByTargetTypeCode(String targetTypeCode) {
        var list = workflowRepository.findByTargetTableName_CodeAndActiveTrue(targetTypeCode);
        if (list == null || list.isEmpty())
            throw new IllegalArgumentException("No active workflow found for target type: " + targetTypeCode);
        if (list.size() > 1)
            throw new IllegalStateException("Multiple active workflows found for target type: " + targetTypeCode + ", please specify workflowCode");
        return list.get(0);
    }
}
