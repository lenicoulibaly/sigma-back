package lenicorp.admin.workflowengine.controller.service;

import lenicorp.admin.workflowengine.controller.repositories.WorkflowStatusGroupRepository;
import lenicorp.admin.security.controller.services.specs.IJwtService;
import lenicorp.admin.workflowengine.model.dtos.WorkflowStatusGroupDTO;
import lenicorp.admin.workflowengine.model.dtos.mapper.WorkflowStatusGroupMapper;
import lenicorp.admin.workflowengine.model.entities.WorkflowStatusGroup;
import lenicorp.admin.utilities.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkflowStatusGroupServiceImpl implements WorkflowStatusGroupService {

    private final WorkflowStatusGroupRepository repository;
    private final lenicorp.admin.workflowengine.controller.repositories.WorkflowStatusRepository statusRepo;
    private final lenicorp.admin.workflowengine.controller.repositories.TransitionRepository transitionRepo;
    private final lenicorp.admin.security.controller.repositories.AuthorityRepo authorityRepo;
    private final IJwtService jwtService;
    private final WorkflowStatusGroupMapper mapper;

    @Override
    @Transactional
    public WorkflowStatusGroupDTO create(WorkflowStatusGroupDTO dto)
    {
        WorkflowStatusGroup entity = mapper.toEntity(dto);
        if (entity.getCode() != null)
        {
            entity.setCode(entity.getCode().toUpperCase());
        }
        if (dto.getAuthorityCodes() != null && !dto.getAuthorityCodes().isEmpty())
        {
            entity.setAuthorities(authorityRepo.findAllById(dto.getAuthorityCodes()));
        }
        entity = repository.save(entity);
        this.updateStatuses(entity, dto.getStatusIds());
        return mapper.toDto(entity);
    }

    @Override
    @Transactional
    public WorkflowStatusGroupDTO update(Long id, WorkflowStatusGroupDTO dto)
    {
        WorkflowStatusGroup entity = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Groupe de statut introuvable avec l'ID : " + id));
        mapper.updateEntity(dto, entity);
        if (entity.getCode() != null)
        {
            entity.setCode(entity.getCode().toUpperCase());
        }
        if (dto.getAuthorityCodes() != null)
        {
            entity.setAuthorities(authorityRepo.findAllById(dto.getAuthorityCodes()));
        } else {
            entity.setAuthorities(null);
        }
        entity = repository.save(entity);
        this.updateStatuses(entity, dto.getStatusIds());
        return mapper.toDto(entity);
    }

    private void updateStatuses(WorkflowStatusGroup group, List<Long> statusIds)
    {
        // 1. Récupérer les statuts actuellement associés au groupe
        List<lenicorp.admin.workflowengine.model.entities.WorkflowStatus> currentStatuses = statusRepo.findAllByGroupsContains(group);

        // Initialiser la liste des IDs cibles pour faciliter la comparaison
        List<Long> targetIds = (statusIds == null) ? new java.util.ArrayList<>() : statusIds;

        // 2. Identifier et retirer le groupe des statuts qui ne sont plus dans la liste cible
        List<lenicorp.admin.workflowengine.model.entities.WorkflowStatus> statusesToRemove = currentStatuses.stream()
                .filter(s -> !targetIds.contains(s.getId()))
                .peek(s -> s.getGroups().remove(group))
                .collect(Collectors.toList());

        if (!statusesToRemove.isEmpty()) {
            statusRepo.saveAll(statusesToRemove);
        }

        // 3. Identifier et ajouter le groupe aux nouveaux statuts
        if (!targetIds.isEmpty()) {
            // On récupère les IDs des statuts déjà associés pour ne pas les traiter à nouveau
            List<Long> alreadyAssociatedIds = currentStatuses.stream()
                    .map(lenicorp.admin.workflowengine.model.entities.WorkflowStatus::getId)
                    .collect(Collectors.toList());

            // On ne charge que les statuts qui ne sont pas encore associés
            List<Long> idsToAdd = targetIds.stream()
                    .filter(id -> !alreadyAssociatedIds.contains(id))
                    .collect(Collectors.toList());

            if (!idsToAdd.isEmpty()) {
                List<lenicorp.admin.workflowengine.model.entities.WorkflowStatus> newStatuses = statusRepo.findAllById(idsToAdd);
                for (lenicorp.admin.workflowengine.model.entities.WorkflowStatus status : newStatuses) {
                    if (status.getGroups() == null) {
                        status.setGroups(new java.util.ArrayList<>());
                    }
                    if (!status.getGroups().contains(group)) {
                        status.getGroups().add(group);
                    }
                }
                statusRepo.saveAll(newStatuses);
            }
        }
    }

    @Override
    @Transactional
    public void delete(Long id)
    {
        if (!repository.existsById(id))
        {
            throw new NoSuchElementException("Groupe de statut introuvable avec l'ID : " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public Page<WorkflowStatusGroupDTO> search(String key, Long workflowId, Pageable pageable)
    {
        String normalizedKey = key == null ? null : "%" + StringUtils.stripAccentsToUpperCase(key) + "%";
        Page<WorkflowStatusGroupDTO> page = repository.searchAccessible(normalizedKey, workflowId, pageable);
        List<WorkflowStatusGroupDTO> content = page.getContent().stream()
                .peek(this::fillDto)
                .collect(Collectors.toList());
        return new org.springframework.data.domain.PageImpl<>(content, pageable, page.getTotalElements());
    }

    @Override
    public List<WorkflowStatusGroupDTO> getAccessibleWorkflowStatusGroupByWorkflowCode(String workflowCode)
    {
        List<WorkflowStatusGroupDTO> dtos = repository.getAccessibleWorkflowStatusGroupByWorkflowCode(workflowCode);
        dtos.forEach(this::fillDto);
        return dtos.stream()
                .filter(dto -> dto.getAuthorityCodes() == null || dto.getAuthorityCodes().isEmpty()
                        || dto.getAuthorityCodes().stream().anyMatch(jwtService::hasPrivilege))
                .collect(Collectors.toList());
    }

    private void fillDto(WorkflowStatusGroupDTO dto)
    {
        repository.findById(dto.getId()).ifPresent(entity -> {
            dto.setStatusIds(mapper.statusIdsFromStatuses(entity.getStatuses()));
            dto.setStatusCodes(mapper.statusCodesFromStatuses(entity.getStatuses()));
            dto.setStatusNames(mapper.statusNamesFromStatuses(entity.getStatuses()));
            dto.setAuthorityCodes(mapper.authorityCodesFromAuthorities(entity.getAuthorities()));
            dto.setAuthorityNames(mapper.authorityNamesFromAuthorities(entity.getAuthorities()));
        });
    }

    @Override
    public List<String> getAuthorityCodes(Long id)
    {
        WorkflowStatusGroup group = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Groupe de statut introuvable avec l'ID : " + id));
        
        java.util.Set<String> authorityCodes = new java.util.HashSet<>();
        
        // Autorités directement liées au groupe
        if (group.getAuthorities() != null)
        {
            group.getAuthorities().forEach(auth -> authorityCodes.add(auth.getCode()));
        }
        
        // Privilèges des transitions dont le statut origine fait partie du groupe
        authorityCodes.addAll(transitionRepo.findPrivilegeCodesByGroupId(id));
        
        return new java.util.ArrayList<>(authorityCodes);
    }
}
