package lenicorp.admin.structures.model.dtos;

import lenicorp.admin.structures.model.entities.Structure;
import lenicorp.admin.structures.model.entities.VStructure;
import lenicorp.admin.types.model.entities.Type;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StrMapper
{
    @Mapping(target = "strTypeName", source = "strType.name")
    @Mapping(target = "strTypeCode", source = "strType.code")
    @Mapping(target = "respoId", ignore = true)
    @Mapping(target = "respoName", ignore = true)
    @Mapping(target = "respoMatricule", ignore = true)
    ReadStrDTO mapToReadStrDTO(Structure vs);

    @Mapping(target = "strTypeName", source = "strTypeName")
    @Mapping(target = "strTypeCode", source = "strTypeCode")
    @Mapping(target = "respoId", ignore = true)
    @Mapping(target = "respoName", ignore = true)
    @Mapping(target = "respoMatricule", ignore = true)
    ReadStrDTO mapToReadStrDTO(VStructure vs);

    /**
     * Méthode pour mapper une liste
     */
    List<ReadStrDTO> mapToReadStrDTOList(List<VStructure> vStructures);

    /**
     * Mapping de CreateOrUpdateStrDTO vers Structure pour création
     */
    @Mapping(target = "strId", ignore = true) // L'ID sera généré automatiquement
    @Mapping(target = "strType", source = "typeCode", qualifiedByName = "mapTypeCode")
    @Mapping(target = "strParent", source = "parentId", qualifiedByName = "mapParentId")
    @Mapping(target = "strChildren", ignore = true)
    Structure mapToStructureForCreate(CreateOrUpdateStrDTO dto);

    /**
     * Mapping de CreateOrUpdateStrDTO vers Structure existante pour mise à jour
     */
    @Mapping(target = "strType", source = "typeCode", qualifiedByName = "mapTypeCode")
    @Mapping(target = "strParent", source = "parentId", qualifiedByName = "mapParentId")
    @Mapping(target = "strChildren", ignore = true)
    Structure updateStructureFromDTO(CreateOrUpdateStrDTO dto, @MappingTarget Structure structure);

    @Mapping(target = "strId", ignore = true)
    @Mapping(target = "strType", source = "typeCode", qualifiedByName = "mapTypeCode")
    @Mapping(target = "strChildren", ignore = true)
    @Mapping(target = "strParent", source = "parentId", qualifiedByName = "mapParentId")
    Structure updateParentFromChangeAnchorDto(ChangeAnchorDTO dto, @MappingTarget Structure structure);

    /**
     * Méthode personnalisée pour mapper le code du type vers l'entité Type
     */
    @Named("mapTypeCode")
    default Type mapTypeCode(String typeCode)
    {
        if (typeCode == null)
        {
            return null;
        }
        return  new Type(typeCode);
    }

    /**
     * Méthode personnalisée pour mapper l'ID parent vers l'entité Structure parente
     */
    @Named("mapParentId")
    default Structure mapParentId(Long parentId)
    {
        if (parentId == null)
        {
            return null;
        }
        return new Structure(parentId);
    }
}