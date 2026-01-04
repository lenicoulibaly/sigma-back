package lenicorp.admin.workflowengine.controller.repositories;

import lenicorp.admin.workflowengine.model.entities.TransitionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransitionRuleRepository extends JpaRepository<TransitionRule, Long> {
    @Query("SELECT tr FROM TransitionRule tr WHERE tr.transition.transitionId = :transitionId AND tr.active = true ORDER BY tr.ordre ASC")
    List<TransitionRule> findActiveRulesByTransitionId(Long transitionId);
}
