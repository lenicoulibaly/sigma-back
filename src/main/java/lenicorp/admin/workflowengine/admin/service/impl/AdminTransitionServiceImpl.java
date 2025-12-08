package lenicorp.admin.workflowengine.admin.service.impl;

import lenicorp.admin.workflowengine.admin.dto.TransitionAdminDTO;
import lenicorp.admin.workflowengine.admin.mapper.TransitionAdminMapper;
import lenicorp.admin.workflowengine.admin.service.AdminTransitionService;
import lenicorp.admin.workflowengine.engine.rules.RuleEvaluationService;
import lenicorp.admin.workflowengine.model.entities.Transition;
import lenicorp.admin.workflowengine.model.repositories.TransitionRepository;
import lenicorp.admin.workflowengine.model.repositories.TransitionRuleRepository;
import lenicorp.admin.workflowengine.model.repositories.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminTransitionServiceImpl implements AdminTransitionService {

    private final TransitionRepository transitionRepo;
    private final TransitionRuleRepository ruleRepo;
    private final WorkflowRepository workflowRepo;
    private final RuleEvaluationService ruleEngine;
    private final TransitionAdminMapper mapper;

    @Override
    public List<TransitionAdminDTO> listAll() {
        return transitionRepo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public TransitionAdminDTO get(String privilegeCode) {
        return transitionRepo.findById(privilegeCode).map(mapper::toDto).orElse(null);
    }

    @Override
    @Transactional
    public TransitionAdminDTO create(TransitionAdminDTO dto) {
        if (dto.getPrivilegeCode() == null || dto.getPrivilegeCode().isBlank()) {
            throw new IllegalArgumentException("privilegeCode is required");
        }
        if (transitionRepo.existsById(dto.getPrivilegeCode())) {
            throw new IllegalStateException("Transition already exists");
        }
        Transition t = mapper.toEntity(dto);
        t.setPrivilegeCode(dto.getPrivilegeCode());
        if (dto.getWorkflowId() != null) {
            t.setWorkflow(workflowRepo.findById(dto.getWorkflowId())
                    .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + dto.getWorkflowId())));
        }
        Transition saved = transitionRepo.save(t);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public TransitionAdminDTO update(String privilegeCode, TransitionAdminDTO dto) {
        return transitionRepo.findById(privilegeCode).map(entity -> {
            dto.setPrivilegeCode(privilegeCode);
            mapper.updateEntity(dto, entity);
            if (dto.getWorkflowId() != null) {
                entity.setWorkflow(workflowRepo.findById(dto.getWorkflowId())
                        .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + dto.getWorkflowId())));
            } else {
                entity.setWorkflow(null);
            }
            Transition saved = transitionRepo.save(entity);
            return mapper.toDto(saved);
        }).orElseThrow(() -> new NoSuchElementException("Transition not found"));
    }

    @Override
    @Transactional
    public void delete(String privilegeCode) {
        if (!transitionRepo.existsById(privilegeCode)) return;
        transitionRepo.deleteById(privilegeCode);
    }

    @Override
    public List<TransitionAdminDTO> listByWorkflow(Long workflowId) {
        return transitionRepo.findAll().stream()
                .filter(t -> t.getWorkflow() != null && Objects.equals(t.getWorkflow().getId(), workflowId))
                .sorted(Comparator.comparing(Transition::getOrdre))
                .map(mapper::toDto).toList();
    }

    @Override
    @Transactional
    public void reorder(List<Map<String, Object>> items) {
        if (items == null) return;
        for (Map<String, Object> item : items) {
            String privilegeCode = String.valueOf(item.get("privilegeCode"));
            Integer ordre = item.get("ordre") == null ? null : Integer.valueOf(String.valueOf(item.get("ordre")));
            if (privilegeCode == null || ordre == null) continue;
            transitionRepo.findById(privilegeCode).ifPresent(t -> {
                t.setOrdre(ordre);
                transitionRepo.save(t);
            });
        }
    }

    @Override
    public String testNextStatus(String privilegeCode, Map<String, Object> facts) {
        return transitionRepo.findById(privilegeCode).map(t -> {
            var rules = ruleRepo.findByTransition_PrivilegeCodeAndActiveTrueOrderByOrdreAsc(privilegeCode);
            String dest = ruleEngine.evaluate(rules, facts != null ? facts : Map.of());
            if (dest == null && t.getDefaultStatutDestination() != null) dest = t.getDefaultStatutDestination().code;
            return dest;
        }).orElse(null);
    }
}
