package lenicorp.admin.workflowengine.model.dtos.mapper;

import lenicorp.admin.workflowengine.model.dtos.WorkflowDTO;
import lenicorp.admin.workflowengine.model.entities.Workflow;
import lenicorp.admin.types.model.entities.Type;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {WorkflowStatusMapper.class})
public interface WorkflowMapper
{

    @Mapping(target = "typeCode", source = "type.code")
    @Mapping(target = "targetTableNameCode", source = "targetTableName.code")
    @Mapping(target = "statuses", source = "statuses")
    WorkflowDTO toDto(Workflow entity);

    @Mapping(target = "type", source = "typeCode")
    @Mapping(target = "targetTableName", source = "targetTableNameCode")
    @Mapping(target = "statuses", ignore = true)
    Workflow toEntity(WorkflowDTO dto);

    default Type map(String code) {
        return code == null || code.isBlank() ? null : new Type(code);
    }

    @Mapping(target = "type", source = "typeCode")
    @Mapping(target = "targetTableName", source = "targetTableNameCode")
    void updateEntity(WorkflowDTO dto, @org.mapstruct.MappingTarget Workflow entity);
}
