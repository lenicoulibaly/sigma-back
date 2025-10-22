package lenicorp.admin.security.model.mappers;

import lenicorp.admin.security.model.dtos.AuthorityDTO;
import lenicorp.admin.security.model.entities.AppAuthority;
import lenicorp.admin.security.model.views.VProfilePrivilege;
import lenicorp.admin.security.model.views.VUserProfile;
import lenicorp.admin.types.model.entities.Type;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface AuthorityMapper
{
    @Mapping(source = "typeName", target = "type.name")
    @Mapping(source = "typeCode", target = "type.code")
    @Mapping(target = "privilegeType", expression = "java(mapToType(dto.getPrivilegeTypeCode()))")
    AppAuthority mapToAutority(AuthorityDTO dto);


    @InheritInverseConfiguration(name = "mapToAutority")
    @Mapping(target = "privilegeTypeCode", source = "privilegeType.code")
    @Mapping(target = "privilegeTypeName", source = "privilegeType.name")
    AuthorityDTO mapToAuthorityDTO(AppAuthority appAuthority);

    @InheritConfiguration(name = "mapToAutority")
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "code", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AppAuthority partialUpdate(AuthorityDTO dto, @MappingTarget AppAuthority appAuthority);

    @Mapping(source = "profileCode", target = "code")
    @Mapping(source = "profileName", target = "name")
    @Mapping(source = "profileDescription", target = "description")
    @Mapping(source = "profileTypeCode", target = "typeCode")
    @Mapping(source = "profileTypeName", target = "typeName")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    AuthorityDTO mapToAuthorityDTO(VUserProfile VUserProfile);

    @Mapping(expression = "java(mapToType(\"PRV\"))", target = "type")
    @Mapping(qualifiedByName = "mapToType", source = "privilegeTypeCode", target = "privilegeType")
    AppAuthority mapToPrivilege(AuthorityDTO dto);

    @Mapping(expression = "java(mapToType(\"ROL\"))", target = "type")
    AppAuthority mapToRole(AuthorityDTO dto);

    @Mapping(expression = "java(mapToType(\"PRFL\"))", target = "type")
    AppAuthority mapToProfile(AuthorityDTO dto);

    @Named("mapToType")
    default Type mapToType(String typeCode)
    {
        if (typeCode == null) return null;
        return new Type(typeCode);
    }

    @Mapping(target = "code", source = "privilegeCode")
    @Mapping(target = "name", source = "privilegeName")
    @Mapping(target = "description", source = "privilegeDescription")
    @Mapping(target = "typeCode", expression = "java(\"PRV\")")
    AuthorityDTO mapToPrivilegeDTO(VProfilePrivilege VProfilePrivilege);

    @Mapping(target = "code", source = "profileCode")
    @Mapping(target = "name", source = "profileName")
    @Mapping(target = "description", source = "profileDescription")
    @Mapping(target = "typeCode", expression = "java(\"PRFL\")")
    AuthorityDTO mapToProfileDTO(VProfilePrivilege VProfilePrivilege);
}