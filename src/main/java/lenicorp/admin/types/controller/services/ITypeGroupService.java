package lenicorp.admin.types.controller.services;


import lenicorp.admin.types.model.dtos.TypeGroupDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ITypeGroupService
{
    Page<TypeGroupDTO> searchTypeGroups(String key, PageRequest pageRequest);

    boolean existsByName(String name, String groupCode);

    boolean existsByName(String name);

    boolean existsByGroupCode(String groupCode);

    String findGroupCodeByTypeCode(String typeCode);

    List<TypeGroupDTO> getAllTypeGroupes();
}
