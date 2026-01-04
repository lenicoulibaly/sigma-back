package lenicorp.admin.workflowengine.controller.repositories;

import lenicorp.admin.workflowengine.model.dtos.TransitionSideEffectDTO;
import lenicorp.admin.workflowengine.model.entities.TransitionSideEffect;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransitionSideEffectRepository extends JpaRepository<TransitionSideEffect, Long> {

    @Query("""
            SELECT new lenicorp.admin.workflowengine.model.dtos.TransitionSideEffectDTO(
                se.id,
                se.transition.transitionId,
                se.name,
                se.actionType,
                se.actionConfig,
                se.ordre
            )
            FROM TransitionSideEffect se
            WHERE se.transition.transitionId = :transitionId
            AND (
                :key IS NULL
                OR UPPER(FUNCTION('unaccent', COALESCE(se.name, ''))) LIKE :key
                OR UPPER(FUNCTION('unaccent', COALESCE(se.actionType, ''))) LIKE :key
                OR UPPER(FUNCTION('unaccent', COALESCE(se.actionConfig, ''))) LIKE :key
            )
            ORDER BY se.ordre ASC
            """)
    Page<TransitionSideEffectDTO> search(@Param("transitionId") Long transitionId, @Param("key") String key, Pageable pageable);

    @Query("""
            SELECT new lenicorp.admin.workflowengine.model.dtos.TransitionSideEffectDTO(
                se.id,
                se.transition.transitionId,
                se.name,
                se.actionType,
                se.actionConfig,
                se.ordre
            )
            FROM TransitionSideEffect se
            WHERE se.transition.transitionId = :transitionId
            ORDER BY se.ordre ASC
            """)
    List<TransitionSideEffectDTO> findByTransitionId(@Param("transitionId") Long transitionId);

    boolean existsByTransitionTransitionIdAndNameAndIdNot(Long transitionId, String name, Long id);
    boolean existsByTransitionTransitionIdAndName(Long transitionId, String name);
}
