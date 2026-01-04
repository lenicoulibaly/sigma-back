package lenicorp.admin.workflowengine.model.dtos.mapper;

import lenicorp.admin.types.model.entities.Type;
import lenicorp.admin.workflowengine.model.dtos.TransitionValidationConfigDTO;
import lenicorp.admin.workflowengine.model.entities.TransitionValidationConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransitionValidationConfigMapper {

    @Mapping(target = "requiredDocTypeCodes", expression = "java(toCodes(entity.getRequiredDocTypes()))")
    TransitionValidationConfigDTO toDto(TransitionValidationConfig entity);

    @Mapping(target = "requiredDocTypes", ignore = true) // handled in service
    TransitionValidationConfig toEntity(TransitionValidationConfigDTO dto);

    default List<String> toCodes(List<Type> types) {
        return types == null ? java.util.List.of() : types.stream().map(t -> t.code).toList();
    }
}
