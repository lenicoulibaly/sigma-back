package lenicorp.admin.workflowengine.model.repositories;

import lenicorp.admin.workflowengine.model.entities.Transition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransitionRepository extends JpaRepository<Transition, String> {
    List<Transition> findByWorkflow_IdAndActiveTrueAndStatutOrigine_CodeOrderByOrdreAsc(Long workflowId, String statutOrigineCode);
}
