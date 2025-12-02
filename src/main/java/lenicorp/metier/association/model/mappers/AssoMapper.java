package lenicorp.metier.association.model.mappers;

import lenicorp.metier.association.model.dtos.CreateAssociationDTO;
import lenicorp.metier.association.model.entities.Association;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class AssoMapper
{
    public abstract Association mapToAssociation(CreateAssociationDTO dto);
}