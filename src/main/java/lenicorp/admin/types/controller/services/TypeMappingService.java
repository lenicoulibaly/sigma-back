package lenicorp.admin.types.controller.services;

import lenicorp.admin.types.controller.repositories.TypeMappingRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class TypeMappingService implements ITypeMappingService
{
    private final TypeMappingRepo typeMappingRepo;

    @Override
    public boolean parentHasDirectSousType(String parentCode, String childCode)
    {
        return typeMappingRepo.parentHasDirectSousType(parentCode, childCode);
    }
    
    /**
     * Find child type codes to remove based on input list
     * @param parentCode Parent type code
     * @param inputSousTypeCodes List of child type codes to keep
     * @return List of child type codes to remove
     */
    @Override
    public List<String> findSousTypeCodesToRemove(String parentCode, List<String> inputSousTypeCodes)
    {
        if (inputSousTypeCodes == null || inputSousTypeCodes.isEmpty()) {
            // If no input codes, all current child types should be removed
            return typeMappingRepo.findChildCodesByParentCode(parentCode);
        }
        
        // Convert input codes to uppercase
        List<String> upperInputCodes = inputSousTypeCodes.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        
        // Find child codes that are not in the input list
        return typeMappingRepo.findChildCodesNotInList(parentCode, upperInputCodes);
    }
    
    /**
     * Find child type codes to add based on input list
     * @param parentCode Parent type code
     * @param inputSousTypeCodes List of child type codes to add
     * @return List of child type codes to add
     */
    @Override
    public List<String> findSousTypeCodesToAdd(String parentCode, List<String> inputSousTypeCodes)
    {
        if (inputSousTypeCodes == null || inputSousTypeCodes.isEmpty()) {
            return List.of();
        }
        
        // Convert input codes to uppercase
        List<String> upperInputCodes = inputSousTypeCodes.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        
        // Find potential child codes
        return typeMappingRepo.findPotentialChildCodes(parentCode, upperInputCodes);
    }
    
    /**
     * Remove child types from a parent
     * @param parentCode Parent type code
     * @param sousTypeCodesToRemove List of child type codes to remove
     */
    @Transactional
    @Override
    public void removeSousTypes(String parentCode, List<String> sousTypeCodesToRemove)
    {
        if (sousTypeCodesToRemove == null || sousTypeCodesToRemove.isEmpty()) {
            return;
        }
        
        // Convert codes to uppercase
        List<String> upperCodes = sousTypeCodesToRemove.stream()
                .map(String::toUpperCase)
                .collect(Collectors.toList());
        
        // Remove child types
        typeMappingRepo.removeChildTypes(parentCode, upperCodes);
    }
}