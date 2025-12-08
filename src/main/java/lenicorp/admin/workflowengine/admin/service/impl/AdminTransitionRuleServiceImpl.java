package lenicorp.admin.workflowengine.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lenicorp.admin.workflowengine.admin.dto.TransitionRuleAdminDTO;
import lenicorp.admin.workflowengine.admin.mapper.TransitionRuleAdminMapper;
import lenicorp.admin.workflowengine.admin.service.AdminTransitionRuleService;
import lenicorp.admin.workflowengine.engine.rules.RuleEvaluationService;
import lenicorp.admin.workflowengine.model.entities.Transition;
import lenicorp.admin.workflowengine.model.entities.TransitionRule;
import lenicorp.admin.workflowengine.model.repositories.TransitionRepository;
import lenicorp.admin.workflowengine.model.repositories.TransitionRuleRepository;
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
    private final TransitionRuleAdminMapper mapper;

    @Override
    public List<TransitionRuleAdminDTO> listAll() {
        return ruleRepo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<TransitionRuleAdminDTO> listByTransition(String privilegeCode) {
        return ruleRepo.findByTransition_PrivilegeCodeAndActiveTrueOrderByOrdreAsc(privilegeCode)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public TransitionRuleAdminDTO get(Long id) {
        return ruleRepo.findById(id).map(mapper::toDto).orElse(null);
    }

    @Override
    @Transactional
    public TransitionRuleAdminDTO create(TransitionRuleAdminDTO dto) {
        TransitionRule r = mapper.toEntity(dto);
        if (dto.getTransitionPrivilegeCode() != null) {
            Transition t = transitionRepo.findById(dto.getTransitionPrivilegeCode())
                    .orElseThrow(() -> new IllegalArgumentException("Transition not found: " + dto.getTransitionPrivilegeCode()));
            r.setTransition(t);
        }
        r = ruleRepo.save(r);
        return mapper.toDto(r);
    }

    @Override
    @Transactional
    public TransitionRuleAdminDTO update(Long id, TransitionRuleAdminDTO dto) {
        return ruleRepo.findById(id).map(entity -> {
            // MapStruct doesn't have update method here; we'll set fields explicitly
            entity.setOrdre(dto.getOrdre() != null ? dto.getOrdre() : 0);
            entity.setActive(dto.getActive() != null ? dto.getActive() : Boolean.TRUE);
            entity.setRuleJson(dto.getRuleJson());
            if (dto.getTransitionPrivilegeCode() != null) {
                Transition t = transitionRepo.findById(dto.getTransitionPrivilegeCode())
                        .orElseThrow(() -> new IllegalArgumentException("Transition not found: " + dto.getTransitionPrivilegeCode()));
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
    public String test(String transitionPrivilegeCode, Map<String, Object> facts) {
        var rules = ruleRepo.findByTransition_PrivilegeCodeAndActiveTrueOrderByOrdreAsc(transitionPrivilegeCode);
        String dest = ruleEngine.evaluate(rules, facts != null ? facts : Map.of());
        if (dest == null) {
            Transition t = transitionRepo.findById(transitionPrivilegeCode).orElse(null);
            if (t != null && t.getDefaultStatutDestination() != null) dest = t.getDefaultStatutDestination().code;
        }
        return dest;
    }
}
