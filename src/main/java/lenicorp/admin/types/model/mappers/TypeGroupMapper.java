package lenicorp.admin.types.model.mappers;

import lenicorp.admin.types.model.dtos.TypeGroupDTO;
import lenicorp.admin.types.model.entities.TypeGroup;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TypeGroupMapper
{
    TypeGroupDTO mapToDto(TypeGroup entity);
    TypeGroup mapToEntity(TypeGroupDTO dto);

    TypeGroup updateTypeGroupFromDto(TypeGroupDTO dto, @MappingTarget TypeGroup t);
}
