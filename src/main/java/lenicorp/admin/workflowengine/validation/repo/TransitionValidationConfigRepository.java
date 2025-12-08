package lenicorp.admin.workflowengine.validation.repo;

import lenicorp.admin.workflowengine.validation.model.TransitionValidationConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransitionValidationConfigRepository extends JpaRepository<TransitionValidationConfig, String> {
}
