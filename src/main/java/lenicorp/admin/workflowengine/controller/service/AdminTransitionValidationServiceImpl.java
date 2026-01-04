package lenicorp.admin.workflowengine.controller.service;

import lenicorp.admin.types.controller.repositories.TypeRepo;
import lenicorp.admin.types.model.entities.Type;
import lenicorp.admin.workflowengine.model.dtos.TransitionValidationConfigDTO;
import lenicorp.admin.workflowengine.model.dtos.mapper.TransitionValidationConfigMapper;
import lenicorp.admin.workflowengine.model.entities.TransitionValidationConfig;
import lenicorp.admin.workflowengine.controller.repositories.TransitionValidationConfigRepository;
import lenicorp.admin.workflowengine.controller.service.AdminTransitionValidationService;
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
    public TransitionValidationConfigDTO get(Long transitionId) {
        return repo.findById(transitionId).map(mapper::toDto).orElse(null);
    }

    @Override
    @Transactional
    public TransitionValidationConfigDTO upsert(Long transitionId, TransitionValidationConfigDTO dto) {
        TransitionValidationConfig cfg = repo.findById(transitionId)
                .orElseGet(() -> {
                    TransitionValidationConfig c = new TransitionValidationConfig();
                    c.setTransitionId(transitionId);
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
    public void delete(Long transitionId) {
        if (repo.existsById(transitionId)) repo.deleteById(transitionId);
    }
}
