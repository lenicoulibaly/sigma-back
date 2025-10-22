package lenicorp.admin.types.controller.services;

import lenicorp.admin.types.controller.repositories.TypeGroupRepo;
import lenicorp.admin.types.model.dtos.TypeGroupDTO;
import lenicorp.admin.types.model.entities.TypeGroup;
import lenicorp.admin.types.model.mappers.TypeGroupMapper;
import lenicorp.admin.utilities.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for TypeGroup operations
 */
@Service @RequiredArgsConstructor
public class TypeGroupService implements ITypeGroupService
{
    private final TypeGroupRepo typeGroupRepo;
    private final TypeGroupMapper typeGroupMapper;

    
    /**
     * Search type groups with pagination
     * @param key Search key
     * @param pageRequest Pagination parameters
     * @return Page of TypeGroupDTO objects
     */
    @Override
    public Page<TypeGroupDTO> searchTypeGroups(String key, PageRequest pageRequest)
    {
        String safeKey = StringUtils.stripAccentsToUpperCase(key);
        int page = pageRequest.getPageNumber();
        int size = pageRequest.getPageSize();
        
        // Get total count
        long totalElements = typeGroupRepo.countTypeGroupsByKey(safeKey);
        
        // Get paginated results
        List<TypeGroup> resultList = typeGroupRepo.searchTypeGroupsByKey(safeKey);
        
        // Apply pagination manually (since we're using a custom Page class)
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, resultList.size());
        
        // Handle out of bounds
        if (fromIndex >= resultList.size()) {
            resultList = List.of();
        } else {
            resultList = resultList.subList(fromIndex, toIndex);
        }
        
        // Map to DTOs
        List<TypeGroupDTO> content = resultList.stream()
                .map(typeGroupMapper::mapToDto)
                .collect(Collectors.toList());
        
        return new PageImpl(content, pageRequest, totalElements);
    }
    
    /**
     * Check if a TypeGroup exists with the given name but different group code
     * @param name Name to check
     * @param groupCode Group code to exclude
     * @return true if exists, false otherwise
     */
    @Override
    public boolean existsByName(String name, String groupCode)
    {
        if (StringUtils.isBlank(name)) return false;
        return typeGroupRepo.existsByNameAndNotGroupCode(name, groupCode);
    }
    
    /**
     * Check if a TypeGroup exists with the given name
     * @param name Name to check
     * @return true if exists, false otherwise
     */
    @Override
    public boolean existsByName(String name)
    {
        if (StringUtils.isBlank(name)) return false;
        return typeGroupRepo.existsByName(name);
    }
    
    /**
     * Check if a TypeGroup exists with the given group code
     * @param groupCode Group code to check
     * @return true if exists, false otherwise
     */
    @Override
    public boolean existsByGroupCode(String groupCode)
    {
        if (StringUtils.isBlank(groupCode)) return false;
        return typeGroupRepo.existsByGroupCode(groupCode);
    }
    
    /**
     * Find group code by type code
     * @param typeCode Type code
     * @return Group code or null if not found
     */
    @Override
    public String findGroupCodeByTypeCode(String typeCode)
    {
        if (StringUtils.isBlank(typeCode)) {
            return null;
        }
        
        try {
            return typeGroupRepo.findGroupCodeByTypeCode(typeCode);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Get all type groups
     * @return List of TypeGroupDTO objects
     */
    @Override
    public List<TypeGroupDTO> getAllTypeGroupes()
    {
        return typeGroupRepo.getAllTypeGroupes();
    }
}