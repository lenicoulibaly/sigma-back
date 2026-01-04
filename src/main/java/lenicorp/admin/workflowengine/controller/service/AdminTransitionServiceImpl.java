package lenicorp.admin.workflowengine.controller.service;

import lenicorp.admin.security.controller.repositories.AuthorityRepo;
import lenicorp.admin.workflowengine.controller.repositories.TransitionValidationConfigRepository;
import lenicorp.admin.workflowengine.model.entities.TransitionValidationConfig;
import lenicorp.admin.workflowengine.model.dtos.TransitionDTO;
import lenicorp.admin.workflowengine.model.dtos.mapper.TransitionMapper;
import lenicorp.admin.workflowengine.engine.rules.RuleEvaluationService;
import lenicorp.admin.workflowengine.model.entities.Transition;
import lenicorp.admin.workflowengine.controller.repositories.TransitionRepository;
import lenicorp.admin.workflowengine.controller.repositories.TransitionRuleRepository;
import lenicorp.admin.workflowengine.controller.repositories.WorkflowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final TransitionMapper mapper;
    private final AuthorityRepo authorityRepo;
    private final lenicorp.admin.types.controller.repositories.TypeRepo typeRepo;
    private final TransitionValidationConfigRepository transitionValidationRepo;

    @Override
    public List<TransitionDTO> listAll() {
        return transitionRepo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public TransitionDTO get(Long transitionId) {
        return transitionRepo.findById(transitionId).map(mapper::toDto).orElse(null);
    }

    @Override
    @Transactional
    public TransitionDTO create(TransitionDTO dto) {
        this.validatePrivilege(dto.getPrivilegeCode());
        Transition t = mapper.toEntity(dto);
        if (t.getValidationConfig() != null) {
            t.getValidationConfig().setTransition(t);
            this.handleRequiredDocTypes(dto, t);
        }
        if (dto.getWorkflowId() != null) {
            t.setWorkflow(workflowRepo.findById(dto.getWorkflowId())
                    .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + dto.getWorkflowId())));
        }
        Transition saved = transitionRepo.save(t);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public TransitionDTO update(Long transitionId, TransitionDTO dto) {
        this.validatePrivilege(dto.getPrivilegeCode());
        return transitionRepo.findById(transitionId).map(entity -> {
            dto.setTransitionId(transitionId);
            mapper.updateEntity(dto, entity);
            if (entity.getValidationConfig() != null) {
                entity.getValidationConfig().setTransition(entity);
                this.handleRequiredDocTypes(dto, entity);
            }
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

    private void handleRequiredDocTypes(TransitionDTO dto, Transition entity) {
        if (dto.getRequiredDocTypeCodes() != null && entity.getValidationConfig() != null) {
            List<lenicorp.admin.types.model.entities.Type> types = dto.getRequiredDocTypeCodes().stream()
                    .map(code -> typeRepo.findById(code)
                            .orElseThrow(() -> new IllegalArgumentException("Unknown Type code: " + code)))
                    .collect(Collectors.toCollection(ArrayList::new));
            entity.getValidationConfig().setRequiredDocTypes(types);
        }
    }

    private void validatePrivilege(String privilegeCode) {
        if (privilegeCode != null && !privilegeCode.isBlank()) {
            if (!authorityRepo.existsByCodeAndType(privilegeCode, "PRV")) {
                throw new IllegalArgumentException("Le code privilège " + privilegeCode + " est invalide ou n'est pas de type PRV");
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long transitionId) {
        if (!transitionRepo.existsById(transitionId)) return;
        transitionRepo.deleteById(transitionId);
    }

    @Override
    public List<TransitionDTO> listByWorkflow(Long workflowId) {
        return transitionRepo.findAll().stream()
                .filter(t -> t.getWorkflow() != null && Objects.equals(t.getWorkflow().getId(), workflowId))
                .sorted(Comparator.comparing(Transition::getOrdre))
                .map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<TransitionDTO> searchByWorkflow(Long workflowId, String key, Pageable pageable) {
        String k = key == null ? "" : key.trim();
        Page<TransitionDTO> page = transitionRepo.searchByWorkflow(workflowId, k, pageable);
        page.getContent().forEach(dto -> {
            TransitionValidationConfig vc = transitionValidationRepo.findById(dto.getTransitionId()).orElse(null);
            if (vc != null) {
                dto.setRequiredDocTypeCodes(vc.getRequiredDocTypes().stream().map(t -> t.code).collect(Collectors.toList()));
            }
        });
        return page;
    }

    @Override
    @Transactional
    public void reorder(List<Map<String, Object>> items) {
        if (items == null) return;
        for (Map<String, Object> item : items) {
            Object idObj = item.get("transitionId");
            if (idObj == null) idObj = item.get("privilegeCode"); // fallback if old key is sent
            if (idObj == null) continue;
            
            Long transitionId = Long.valueOf(String.valueOf(idObj));
            Integer ordre = item.get("ordre") == null ? null : Integer.valueOf(String.valueOf(item.get("ordre")));
            if (ordre == null) continue;
            transitionRepo.findById(transitionId).ifPresent(t -> {
                t.setOrdre(ordre);
                transitionRepo.save(t);
            });
        }
    }

    @Override
    public String testNextStatus(Long transitionId, Map<String, Object> facts) {
        return transitionRepo.findById(transitionId).map(t -> {
            var rules = ruleRepo.findActiveRulesByTransitionId(transitionId);
            String dest = ruleEngine.evaluate(rules, facts != null ? facts : Map.of());
            if (dest == null && t.getDefaultStatutDestination() != null) dest = t.getDefaultStatutDestination().code;
            return dest;
        }).orElse(null);
    }
}
