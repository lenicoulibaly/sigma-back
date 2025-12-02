package lenicorp.metier.association.model.mappers;

import lenicorp.metier.association.model.dtos.CreateSectionDTO;
import lenicorp.metier.association.model.dtos.ReadSectionDTO;
import lenicorp.metier.association.model.entities.Section;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class SectionMapper
{
    @Mapping(target = "association", expression = "java(dto.getAssoId() == null ? null : new lenicorp.metier.association.model.entities.Association(dto.getAssoId()))")
    @Mapping(target = "strTutelle", expression = "java(dto.getStrId() == null ? null : new lenicorp.admin.structures.model.entities.Structure(dto.getStrId()))")
    public abstract Section mapToSection(CreateSectionDTO dto);

    @Mapping(target = "assoId", source = "association.assoId")
    @Mapping(target = "assoName", source = "association.assoName")
    @Mapping(target = "strName", source = "strTutelle.strName")
    @Mapping(target = "strSigle", source = "strTutelle.strSigle")
    public abstract ReadSectionDTO mapToReadSectionDTO(Section section);
}
