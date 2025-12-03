package lenicorp.metier.association.model.mappers;

import lenicorp.admin.security.model.dtos.CreateUserDTO;
import lenicorp.admin.security.model.entities.AppUser;
import lenicorp.metier.association.model.dtos.AdhesionDTO;
import lenicorp.metier.association.model.entities.Adhesion;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import lenicorp.metier.association.model.dtos.CreateDemandeAdhesionDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class AdhesionMapper
{
    public abstract Adhesion mapToAdhesion(AdhesionDTO dto);

    public abstract AdhesionDTO mapToAdhesionDto(AppUser user);

    // Mapping de AdhesionDTO vers CreateUserDTO
    public abstract CreateUserDTO mapToCreateUserDto(AdhesionDTO dto);

    // Mapping de AdhesionDTO vers CreateDemandeAdhesionDTO
    // Les champs accepteRgpd et accepteCharte sont désormais portés par AdhesionDTO et seront mappés automatiquement
    public abstract CreateDemandeAdhesionDTO mapToCreateDemandeAdhesionDto(AdhesionDTO dto);
}
