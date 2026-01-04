package lenicorp.admin.workflowengine.controller.service;

import lenicorp.admin.utilities.StringUtils;
import lenicorp.admin.workflowengine.controller.repositories.TransitionSideEffectRepository;
import lenicorp.admin.workflowengine.model.dtos.TransitionSideEffectDTO;
import lenicorp.admin.workflowengine.model.dtos.mapper.TransitionSideEffectMapper;
import lenicorp.admin.workflowengine.model.entities.TransitionSideEffect;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TransitionSideEffectServiceImpl implements TransitionSideEffectService {

    private final TransitionSideEffectRepository repository;
    private final TransitionSideEffectMapper mapper;

    @Override
    @Transactional
    public TransitionSideEffectDTO create(TransitionSideEffectDTO dto) {
        TransitionSideEffect entity = mapper.toEntity(dto);
        entity = repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public TransitionSideEffectDTO update(Long id, TransitionSideEffectDTO dto) {
        TransitionSideEffect entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Effet de bord introuvable avec l'ID : " + id));
        mapper.updateEntity(dto, entity);
        entity = repository.save(entity);
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Effet de bord introuvable avec l'ID : " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public Page<TransitionSideEffectDTO> search(Long transitionId, String key, Pageable pageable) {
        String normalizedKey = key == null ? null : "%" + StringUtils.stripAccentsToUpperCase(key) + "%";
        return repository.search(transitionId, normalizedKey, pageable);
    }

    @Override
    public List<TransitionSideEffectDTO> findByTransitionId(Long transitionId) {
        return repository.findByTransitionId(transitionId);
    }
}
