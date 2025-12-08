package lenicorp.admin.workflowengine.admin.mapper;

import lenicorp.admin.workflowengine.admin.dto.WorkflowAdminDTO;
import lenicorp.admin.workflowengine.model.entities.Workflow;
import lenicorp.admin.types.model.entities.Type;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkflowAdminMapper {

    @Mapping(target = "typeCode", source = "type.code")
    @Mapping(target = "targetTableNameCode", source = "targetTableName.code")
    WorkflowAdminDTO toDto(Workflow entity);

    @Mapping(target = "type", source = "typeCode")
    @Mapping(target = "targetTableName", source = "targetTableNameCode")
    Workflow toEntity(WorkflowAdminDTO dto);

    default Type map(String code) {
        return code == null || code.isBlank() ? null : new Type(code);
    }

    @Mapping(target = "type", source = "typeCode")
    @Mapping(target = "targetTableName", source = "targetTableNameCode")
    void updateEntity(WorkflowAdminDTO dto, @org.mapstruct.MappingTarget Workflow entity);
}
