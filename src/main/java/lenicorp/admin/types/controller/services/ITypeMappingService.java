package lenicorp.admin.types.controller.services;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ITypeMappingService
{
    boolean parentHasDirectSousType(String parentCode, String childCode);

    List<String> findSousTypeCodesToRemove(String parentCode, List<String> inputSousTypeCodes);

    List<String> findSousTypeCodesToAdd(String parentCode, List<String> inputSousTypeCodes);

    @Transactional
    void removeSousTypes(String parentCode, List<String> sousTypeCodesToRemove);
}
