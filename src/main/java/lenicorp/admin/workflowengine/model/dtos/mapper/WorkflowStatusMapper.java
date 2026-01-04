package lenicorp.admin.workflowengine.model.dtos.mapper;

import lenicorp.admin.types.model.entities.Type;
import lenicorp.admin.workflowengine.model.dtos.WorkflowStatusDTO;
import lenicorp.admin.workflowengine.model.entities.WorkflowStatus;
import lenicorp.admin.workflowengine.model.entities.WorkflowStatusGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkflowStatusMapper
{

    @Mapping(target = "workflow", ignore = true)
    @Mapping(target = "status", source = "statusCode", qualifiedByName = "typeFromCode")
    @Mapping(target = "regulatoryDurationUnit", source = "regulatoryDurationUnitCode", qualifiedByName = "typeFromCode")
    @Mapping(target = "groups", source = "groupIds", qualifiedByName = "groupsFromIds")
    WorkflowStatus toEntity(WorkflowStatusDTO dto);

    @Mapping(target = "statusCode", source = "status.code")
    @Mapping(target = "regulatoryDurationUnitCode", source = "regulatoryDurationUnit.code")
    @Mapping(target = "groupIds", source = "groups", qualifiedByName = "groupIdsFromGroups")
    @Mapping(target = "groupCodes", source = "groups", qualifiedByName = "groupCodesFromGroups")
    @Mapping(target = "groupNames", source = "groups", qualifiedByName = "groupNamesFromGroups")
    WorkflowStatusDTO toDto(WorkflowStatus entity);

    @Named("typeFromCode")
    default Type typeFromCode(String code) {
        return (code == null || code.isBlank()) ? null : new Type(code);
    }

    @Named("groupsFromIds")
    default List<WorkflowStatusGroup> groupsFromIds(List<Long> ids) {
        if (ids == null) return null;
        return ids.stream().map(id -> {
            WorkflowStatusGroup group = new WorkflowStatusGroup();
            group.setId(id);
            return group;
        }).collect(Collectors.toList());
    }

    @Named("groupIdsFromGroups")
    default List<Long> groupIdsFromGroups(List<WorkflowStatusGroup> groups) {
        if (groups == null) return null;
        return groups.stream().map(WorkflowStatusGroup::getId).collect(Collectors.toList());
    }

    @Named("groupCodesFromGroups")
    default List<String> groupCodesFromGroups(List<WorkflowStatusGroup> groups) {
        if (groups == null) return null;
        return groups.stream().map(WorkflowStatusGroup::getCode).collect(Collectors.toList());
    }

    @Named("groupNamesFromGroups")
    default List<String> groupNamesFromGroups(List<WorkflowStatusGroup> groups) {
        if (groups == null) return null;
        return groups.stream().map(WorkflowStatusGroup::getName).collect(Collectors.toList());
    }
}
