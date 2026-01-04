package lenicorp.admin.workflowengine.admin.service.impl;
package lenicorp.admin.workflowengine.controller.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lenicorp.admin.workflowengine.model.dtos.TransitionRuleDTO;
import lenicorp.admin.workflowengine.model.dtos.mapper.TransitionRuleMapper;
import lenicorp.admin.workflowengine.engine.rules.RuleEvaluationService;
import lenicorp.admin.workflowengine.model.entities.Transition;
import lenicorp.admin.workflowengine.model.entities.TransitionRule;
import lenicorp.admin.workflowengine.controller.repositories.TransitionRepository;
import lenicorp.admin.workflowengine.controller.repositories.TransitionRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminTransitionRuleServiceImpl implements AdminTransitionRuleService {

    private final TransitionRuleRepository ruleRepo;
    private final TransitionRepository transitionRepo;
    private final RuleEvaluationService ruleEngine;
    private final ObjectMapper objectMapper;
    private final TransitionRuleMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<TransitionRuleDTO> listAll() {
        return ruleRepo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransitionRuleDTO> listByTransition(Long transitionId) {
        return ruleRepo.findActiveRulesByTransitionId(transitionId)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TransitionRuleDTO get(Long id) {
        return ruleRepo.findById(id).map(mapper::toDto).orElse(null);
    }

    @Override
    @Transactional
    public TransitionRuleDTO create(TransitionRuleDTO dto) {
        TransitionRule r = mapper.toEntity(dto);
        if (dto.getTransitionId() != null) {
            Transition t = transitionRepo.findById(dto.getTransitionId())
                    .orElseThrow(() -> new IllegalArgumentException("Transition not found: " + dto.getTransitionId()));
            r.setTransition(t);
        }
        r = ruleRepo.save(r);
        return mapper.toDto(r);
    }

    @Override
    @Transactional
    public TransitionRuleDTO update(Long id, TransitionRuleDTO dto) {
        return ruleRepo.findById(id).map(entity -> {
            // MapStruct doesn't have update method here; we'll set fields explicitly
            entity.setOrdre(dto.getOrdre() != null ? dto.getOrdre() : 0);
            entity.setActive(dto.getActive() != null ? dto.getActive() : Boolean.TRUE);
            entity.setRuleJson(dto.getRuleJson());
            if (dto.getTransitionId() != null) {
                Transition t = transitionRepo.findById(dto.getTransitionId())
                        .orElseThrow(() -> new IllegalArgumentException("Transition not found: " + dto.getTransitionId()));
                entity.setTransition(t);
            } else {
                entity.setTransition(null);
            }
            if (dto.getStatutDestinationCode() != null) {
                entity.setStatutDestination(new lenicorp.admin.types.model.entities.Type(dto.getStatutDestinationCode()));
            } else {
                entity.setStatutDestination(null);
            }
            TransitionRule saved = ruleRepo.save(entity);
            return mapper.toDto(saved);
        }).orElseThrow(() -> new NoSuchElementException("TransitionRule not found"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!ruleRepo.existsById(id)) return;
        ruleRepo.deleteById(id);
    }

    @Override
    public boolean validateJson(String ruleJson) {
        try {
            objectMapper.readTree(ruleJson);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String test(Long transitionId, Map<String, Object> facts) {
        var rules = ruleRepo.findActiveRulesByTransitionId(transitionId);
        String dest = ruleEngine.evaluate(rules, facts != null ? facts : Map.of());
        if (dest == null) {
            Transition t = transitionRepo.findById(transitionId).orElse(null);
            if (t != null && t.getDefaultStatutDestination() != null) dest = t.getDefaultStatutDestination().code;
        }
        return dest;
    }
}
