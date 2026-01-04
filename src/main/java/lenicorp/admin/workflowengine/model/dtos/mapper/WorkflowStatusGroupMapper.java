package lenicorp.admin.workflowengine.model.dtos.mapper;

import lenicorp.admin.workflowengine.model.dtos.WorkflowStatusGroupDTO;
import lenicorp.admin.workflowengine.model.entities.WorkflowStatus;
import lenicorp.admin.workflowengine.model.entities.WorkflowStatusGroup;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkflowStatusGroupMapper {
    @Mapping(target = "statuses", ignore = true)
    @Mapping(target = "authorities", source = "authorityCodes", qualifiedByName = "authoritiesFromCodes")
    WorkflowStatusGroup toEntity(WorkflowStatusGroupDTO dto);

    @Mapping(target = "statusIds", source = "statuses", qualifiedByName = "statusIdsFromStatuses")
    @Mapping(target = "statusCodes", source = "statuses", qualifiedByName = "statusCodesFromStatuses")
    @Mapping(target = "statusNames", source = "statuses", qualifiedByName = "statusNamesFromStatuses")
    @Mapping(target = "authorityCodes", source = "authorities", qualifiedByName = "authorityCodesFromAuthorities")
    @Mapping(target = "authorityNames", source = "authorities", qualifiedByName = "authorityNamesFromAuthorities")
    WorkflowStatusGroupDTO toDto(WorkflowStatusGroup entity);

    @Mapping(target = "statuses", ignore = true)
    @Mapping(target = "authorities", source = "authorityCodes", qualifiedByName = "authoritiesFromCodes")
    void updateEntity(WorkflowStatusGroupDTO dto, @MappingTarget WorkflowStatusGroup entity);

    @Named("statusIdsFromStatuses")
    default List<Long> statusIdsFromStatuses(List<WorkflowStatus> statuses) {
        if (statuses == null) return null;
        return statuses.stream().map(WorkflowStatus::getId).collect(Collectors.toList());
    }

    @Named("statusCodesFromStatuses")
    default List<String> statusCodesFromStatuses(List<WorkflowStatus> statuses) {
        if (statuses == null) return null;
        return statuses.stream()
                .filter(s -> s.getStatus() != null)
                .map(s -> s.getStatus().code)
                .collect(Collectors.toList());
    }

    @Named("statusNamesFromStatuses")
    default List<String> statusNamesFromStatuses(List<WorkflowStatus> statuses) {
        if (statuses == null) return null;
        return statuses.stream()
                .filter(s -> s.getStatus() != null)
                .map(s -> s.getStatus().name)
                .collect(Collectors.toList());
    }

    @Named("authoritiesFromCodes")
    default List<lenicorp.admin.security.model.entities.AppAuthority> authoritiesFromCodes(List<String> codes) {
        if (codes == null) return null;
        return codes.stream().map(lenicorp.admin.security.model.entities.AppAuthority::new).collect(Collectors.toList());
    }

    @Named("authorityCodesFromAuthorities")
    default List<String> authorityCodesFromAuthorities(List<lenicorp.admin.security.model.entities.AppAuthority> authorities) {
        if (authorities == null) return null;
        return authorities.stream().map(lenicorp.admin.security.model.entities.AppAuthority::getCode).collect(Collectors.toList());
    }

    @Named("authorityNamesFromAuthorities")
    default List<String> authorityNamesFromAuthorities(List<lenicorp.admin.security.model.entities.AppAuthority> authorities) {
        if (authorities == null) return null;
        return authorities.stream().map(lenicorp.admin.security.model.entities.AppAuthority::getName).collect(Collectors.toList());
    }
}
