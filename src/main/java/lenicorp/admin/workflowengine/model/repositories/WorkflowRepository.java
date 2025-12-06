package lenicorp.admin.workflowengine.model.repositories;

import lenicorp.admin.workflowengine.model.entities.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    Optional<Workflow> findByCodeAndActiveTrue(String code);

    List<Workflow> findByTargetTableName_CodeAndActiveTrue(String targetTableNameCode);
}
