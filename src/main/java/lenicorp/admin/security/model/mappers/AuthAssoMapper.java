package lenicorp.admin.security.model.mappers;

import lenicorp.admin.security.model.dtos.UserProfileAssoDTO;
import lenicorp.admin.security.model.entities.AuthAssociation;
import lenicorp.admin.types.model.entities.Type;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthAssoMapper
{
    @Mapping(source = "strName", target = "structure.strName")
    @Mapping(source = "strId", target = "structure.strId")
    @Mapping(source = "profileName", target = "profile.name")
    @Mapping(source = "profileCode", target = "profile.code")
    @Mapping(source = "email", target = "user.email")
    @Mapping(source = "userId", target = "user.userId")
    @Mapping(source = "assoId", target = "assoId")
    @Mapping(source = "sectionId", target = "sectionId")
    @Mapping(expression = "java(mapToType(\"USR_PRFL\"))", target = "type")
    @Mapping(source = "userProfileAssTypeCode", target = "userProfileAssType.code")
    AuthAssociation toEntity(UserProfileAssoDTO userProfileAssoDTO);

    @InheritInverseConfiguration(name = "toEntity")
    UserProfileAssoDTO toDto(AuthAssociation authAssociation);

    @InheritConfiguration(name = "toEntity")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AuthAssociation partialUpdate(UserProfileAssoDTO userProfileAssoDTO, @MappingTarget AuthAssociation authAssociation);

    default Type mapToType(String typeCode)
    {
        if (typeCode == null) return null;
        return new Type(typeCode);
    }
}