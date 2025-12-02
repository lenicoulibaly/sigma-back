package lenicorp.metier.association.model.mappers;

import lenicorp.admin.security.model.dtos.CreateUserDTO;
import lenicorp.admin.security.model.entities.AppUser;
import lenicorp.metier.association.model.dtos.AdhesionDTO;
import lenicorp.metier.association.model.entities.Adhesion;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class AdhesionMapper
{
    public abstract Adhesion mapToAdhesion(AdhesionDTO dto);

    public abstract AdhesionDTO mapToAdhesionDto(AppUser user);

    // Mapping de AdhesionDTO vers CreateUserDTO
    public abstract CreateUserDTO mapToCreateUserDto(AdhesionDTO dto);
}
