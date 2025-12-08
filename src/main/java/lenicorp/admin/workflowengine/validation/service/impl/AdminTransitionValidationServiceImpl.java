package lenicorp.admin.workflowengine.validation.service.impl;

import lenicorp.admin.types.controller.repositories.TypeRepo;
import lenicorp.admin.types.model.entities.Type;
import lenicorp.admin.workflowengine.validation.dto.TransitionValidationConfigDTO;
import lenicorp.admin.workflowengine.validation.mapper.TransitionValidationConfigMapper;
import lenicorp.admin.workflowengine.validation.model.TransitionValidationConfig;
import lenicorp.admin.workflowengine.validation.repo.TransitionValidationConfigRepository;
import lenicorp.admin.workflowengine.validation.service.AdminTransitionValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminTransitionValidationServiceImpl implements AdminTransitionValidationService {
    private final TransitionValidationConfigRepository repo;
    private final TypeRepo typeRepo;
    private final TransitionValidationConfigMapper mapper;

    @Override
    public TransitionValidationConfigDTO get(String transitionPrivilegeCode) {
        return repo.findById(transitionPrivilegeCode).map(mapper::toDto).orElse(null);
    }

    @Override
    @Transactional
    public TransitionValidationConfigDTO upsert(String transitionPrivilegeCode, TransitionValidationConfigDTO dto) {
        TransitionValidationConfig cfg = repo.findById(transitionPrivilegeCode)
                .orElseGet(() -> {
                    TransitionValidationConfig c = new TransitionValidationConfig();
                    c.setTransitionPrivilegeCode(transitionPrivilegeCode);
                    return c;
                });
        boolean commentRequired = Boolean.TRUE.equals(dto.getCommentRequired());
        cfg.setCommentRequired(commentRequired);

        // Resolve required types
        List<String> codes = dto.getRequiredDocTypeCodes();
        if (codes != null) {
            List<Type> types = codes.stream()
                    .map(code -> typeRepo.findById(code)
                            .orElseThrow(() -> new IllegalArgumentException("Unknown Type code: " + code)))
                    .toList();
            cfg.setRequiredDocTypes(types);
        } else {
            cfg.setRequiredDocTypes(List.of());
        }
        cfg = repo.save(cfg);
        return mapper.toDto(cfg);
    }

    @Override
    @Transactional
    public void delete(String transitionPrivilegeCode) {
        if (repo.existsById(transitionPrivilegeCode)) repo.deleteById(transitionPrivilegeCode);
    }
}
