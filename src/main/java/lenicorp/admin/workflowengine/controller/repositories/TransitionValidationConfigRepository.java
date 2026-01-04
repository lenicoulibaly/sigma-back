package lenicorp.admin.workflowengine.controller.repositories;

import lenicorp.admin.workflowengine.model.entities.TransitionValidationConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransitionValidationConfigRepository extends JpaRepository<TransitionValidationConfig, Long> {
}
