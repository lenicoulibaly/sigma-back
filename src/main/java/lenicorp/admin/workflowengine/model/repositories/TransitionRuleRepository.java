package lenicorp.admin.workflowengine.model.repositories;

import lenicorp.admin.workflowengine.model.entities.TransitionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransitionRuleRepository extends JpaRepository<TransitionRule, Long> {
    List<TransitionRule> findByTransition_PrivilegeCodeAndActiveTrueOrderByOrdreAsc(String transitionPrivilegeCode);

    @Query("SELECT tr FROM TransitionRule tr WHERE tr.transition.privilegeCode = ?1 ORDER BY tr.ordre ASC")
    List<TransitionRule> findByTransitionPrvCode(String privilegeCode); // in case Id exists later
}
