package lenicorp.admin.workflowengine.model.repositories;

import lenicorp.admin.workflowengine.model.entities.TransitionRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransitionRuleRepository extends JpaRepository<TransitionRule, Long> {
    List<TransitionRule> findByTransition_PrivilegeCodeAndActiveTrueOrderByOrdreAsc(String transitionPrivilegeCode);
    List<TransitionRule> findByTransition_IdOrderByOrdreAsc(Long transitionId); // in case Id exists later
}
