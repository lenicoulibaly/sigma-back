
package lenicorp.admin.types.controller.services;


import lenicorp.admin.types.model.dtos.TypeDTO;
import lenicorp.admin.types.model.dtos.TypeGroupDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ITypeService
{
    TypeDTO createType(TypeDTO dto);

    TypeDTO updateType(TypeDTO dto);

    Page<TypeDTO> searchTypes(String key, List<String> groupCodes, PageRequest pageRequest);

    TypeGroupDTO createTypeGroup(TypeGroupDTO dto);

    TypeGroupDTO updateTypeGroup(TypeGroupDTO dto);

    Page<TypeGroupDTO> searchTypeGroups(String key, Pageable pageable);

    List<TypeDTO> getDirectSousTypes(String parentCode);

    boolean parentHasDirectSousType(String parentCode, String childCode);

    boolean parentHasDistantSousType(String parentCode, String childCode);

    void setSousTypes(TypeDTO dto);

    List<TypeDTO> getPossibleSousTypes(String parentCode);

    List<TypeDTO> getTypesByGroupCode(String groupCode);

    List<TypeGroupDTO> getAllTypeGroups();
}