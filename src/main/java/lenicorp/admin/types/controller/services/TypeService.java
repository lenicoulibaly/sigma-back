
package lenicorp.admin.types.controller.services;

import lenicorp.admin.exceptions.AppException;
import lenicorp.admin.security.controller.services.specs.IJwtService;
import lenicorp.admin.structures.controller.repositories.VStrRepo;
import lenicorp.admin.structures.model.entities.VStructure;
import lenicorp.admin.types.controller.repositories.TypeGroupRepo;
import lenicorp.admin.types.controller.repositories.TypeMappingRepo;
import lenicorp.admin.types.controller.repositories.TypeRepo;
import lenicorp.admin.types.model.dtos.TypeDTO;
import lenicorp.admin.types.model.dtos.TypeGroupDTO;
import lenicorp.admin.types.model.entities.Type;
import lenicorp.admin.types.model.entities.TypeGroup;
import lenicorp.admin.types.model.entities.TypeMapping;
import lenicorp.admin.types.model.mappers.TypeGroupMapper;
import lenicorp.admin.types.model.mappers.TypeMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TypeService implements ITypeService
{
    private final TypeRepo typeRepo;
    private final TypeGroupRepo typeGroupRepo;
    private final TypeMappingRepo tmRepo;
    private final TypeMapper typeMapper;
    private final TypeGroupMapper typeGroupMapper;
    private final IJwtService jwtService;
    private final VStrRepo vStrRepo;

    @Override
    @Transactional
    public TypeDTO createType(TypeDTO dto)
    {
        // Validation de l'existence du code
        if (typeRepo.existsByCode(dto.getCode()))
        {
            throw new AppException("Un type avec ce code existe déjà : " + dto.getCode());
        }

        // Validation de l'existence du nom
        if (typeRepo.existsByName(dto.getName()))
        {
            throw new AppException("Un type avec ce nom existe déjà : " + dto.getName());
        }

        Type type = typeMapper.mapToType(dto);
        type = typeRepo.save(type);
        if(dto.getParentTypeCodes() != null && !dto.getParentTypeCodes().isEmpty())
        {
            for (String parentCode : dto.getParentTypeCodes())
            {
                if (!typeRepo.existsByCodeAndGroupCode(parentCode, dto.getGroupCode()))
                {
                    throw new AppException("Le parent " + parentCode + " n'existe pas ou n'appartient pas au même groupe.");
                }
                tmRepo.save(new TypeMapping(null, new Type(parentCode), type));
            }
        }
        TypeDTO result = typeMapper.mapToDto(type);
        result.setParentTypeCodes(typeRepo.getParentTypeCodes(type.code));
        return result;
    }

    @Override
    @Transactional
    public TypeDTO updateType(TypeDTO dto)
    {
        Type existingType = typeRepo.findById(dto.getCode()).orElseThrow(()->new AppException("Type introuvable"));
        if (existingType == null) throw new AppException("Type non trouvé avec le code : " + dto.getCode());
        if (typeRepo.existsByName(dto.getName(), dto.getCode()))
            throw new AppException("Un autre type avec ce nom existe déjà : " + dto.getName());
        existingType = typeMapper.mapToType(dto, existingType);
        if (dto.getGroupCode() != null) existingType.typeGroup = new TypeGroup(dto.getGroupCode());
        existingType = typeRepo.save(existingType);

        if (dto.getParentTypeCodes() != null)
        {
            List<String> toRemove = tmRepo.findParentCodesToRemove(existingType.code, dto.getParentTypeCodes());
            if (!toRemove.isEmpty()) tmRepo.removeParents(existingType.code, toRemove);

            List<String> toAdd = tmRepo.findParentCodesToAdd(existingType.code, dto.getParentTypeCodes());
            for (String parentCode : toAdd)
            {
                if (!typeRepo.existsByCodeAndGroupCode(parentCode, dto.getGroupCode()))
                {
                    throw new AppException("Le parent " + parentCode + " n'existe pas ou n'appartient pas au même groupe.");
                }
                if (this.parentHasDistantSousType(existingType.code, parentCode))
                {
                    throw new AppException("Le type " + parentCode + " est déjà un descendant du type " + existingType.code + ". Association impossible.");
                }
                tmRepo.save(new TypeMapping(null, new Type(parentCode), existingType));
            }
        }

        TypeDTO result = typeMapper.mapToDto(existingType);
        result.setParentTypeCodes(typeRepo.getParentTypeCodes(existingType.code));
        return result;
    }

    @Override
    public Page<TypeDTO> searchTypes(String key, List<String> groupCodes, PageRequest pageRequest)
    {
        boolean hasGroupCodeFilter = groupCodes != null && !groupCodes.isEmpty();
        Page<TypeDTO> result = typeRepo.searchTypes(key, groupCodes, hasGroupCodeFilter, pageRequest);
        result.getContent().forEach(dto -> {
            dto.setParentTypeCodes(typeRepo.getParentTypeCodes(dto.getCode()));
        });
        return result;
    }

    @Override
    @Transactional
    public TypeGroupDTO createTypeGroup(TypeGroupDTO dto)
    {
        // Validation de l'existence du code
        if (typeGroupRepo.existsByGroupCode(dto.getGroupCode()))
        {
            throw new AppException("Un groupe avec ce code existe déjà : " + dto.getGroupCode());
        }

        // Validation de l'existence du nom
        if (typeGroupRepo.existsByName(dto.getName()))
        {
            throw new AppException("Un groupe avec ce nom existe déjà : " + dto.getName());
        }

        TypeGroup typeGroup = typeGroupMapper.mapToEntity(dto);
        typeGroup = typeGroupRepo.save(typeGroup);
        return typeGroupMapper.mapToDto(typeGroup);
    }

    @Override
    @Transactional
    public TypeGroupDTO updateTypeGroup(TypeGroupDTO dto)
    {
        TypeGroup existingGroup = typeGroupRepo.findById(dto.getGroupCode()).orElseThrow(()->new AppException("Groupe introuvable"));
        if (existingGroup == null)
        {
            throw new AppException("Groupe non trouvé avec le code : " + dto.getGroupCode());
        }

        // Validation du nom (pas le même nom pour un autre code)
        if (typeGroupRepo.existsByName(dto.getName(), dto.getGroupCode()))
        {
            throw new AppException("Un autre groupe avec ce nom existe déjà : " + dto.getName());
        }

        existingGroup.name = dto.getName();
        existingGroup = typeGroupRepo.save(existingGroup);
        return typeGroupMapper.mapToDto(existingGroup);
    }

    @Override
    public Page<TypeGroupDTO> searchTypeGroups(String key, Pageable pageable)
    {
        return typeGroupRepo.searchTypeGroups(key, pageable);
    }

    @Override
    public List<TypeDTO> getDirectSousTypes(String parentCode)
    {
        List<TypeDTO> result = typeRepo.findDirectSousTypes(parentCode);
        result.forEach(dto -> dto.setParentTypeCodes(typeRepo.getParentTypeCodes(dto.getCode())));
        return result;
    }

    @Override
    public boolean parentHasDirectSousType(String parentCode, String childCode)
    {
        return tmRepo.parentHasDirectSousType(parentCode, childCode);
    }

    @Override
    public boolean parentHasDistantSousType(String parentCode, String childCode)
    {
        if (parentHasDirectSousType(parentCode, childCode)) return true;
        if (!typeRepo.existsByCode(parentCode) || !typeRepo.existsByCode(childCode)) return false;
        return typeRepo.findDirectSousTypes(parentCode).stream().anyMatch(st -> parentHasDistantSousType(st.getCode(), childCode));
    }

    @Override
    @Transactional
    public void setSousTypes(TypeDTO dto)
    {
        Type type = typeRepo.findById(dto.getCode()).orElseThrow(() -> new AppException("Code de type introuvable : " + dto.getCode()));
        List<String> inputSousTypeCodes = dto.getSousTypeCodes() == null || dto.getSousTypeCodes().isEmpty() ? Collections.emptyList() : dto.getSousTypeCodes();

        List<String> sousTypeCodesToRemove = inputSousTypeCodes.isEmpty() ? tmRepo.findAllSousTypeCodes(dto.getCode()) : tmRepo.findSousTypeCodesToRemove(dto.getCode(), inputSousTypeCodes);
        List<String> sousTypeCodesToAdd = tmRepo.findSousTypeCodesToAdd(dto.getCode(), inputSousTypeCodes);
        if (sousTypeCodesToRemove != null && !sousTypeCodesToRemove.isEmpty())
            tmRepo.removeSousTypes(dto.getCode(), sousTypeCodesToRemove);

        if (sousTypeCodesToAdd != null && !sousTypeCodesToAdd.isEmpty())
        {
            sousTypeCodesToAdd = sousTypeCodesToAdd.stream().filter(sta -> !this.parentHasDistantSousType(sta, dto.getCode())).collect(Collectors.toList());
            sousTypeCodesToAdd.forEach(st ->
            {
                tmRepo.save(new TypeMapping(null, new Type(dto.getCode()), new Type(st)));
            });
        }
    }

    @Override
    public List<TypeDTO> getPossibleSousTypes(String parentCode)
    {
        List<TypeDTO> result = typeRepo.findByTypeGroup(typeGroupRepo.findGroupCodeByTypeCode(parentCode)).stream()
                .filter(t -> !this.parentHasDistantSousType(t.getCode(), parentCode) && !t.getCode().equals(parentCode))
                .collect(Collectors.toList());
        result.forEach(dto -> dto.setParentTypeCodes(typeRepo.getParentTypeCodes(dto.getCode())));
        return result;
    }

    @Override
    public List<TypeDTO> getPossibleParents(String typeCode)
    {
        List<TypeDTO> result = typeRepo.findByTypeGroup(typeGroupRepo.findGroupCodeByTypeCode(typeCode)).stream()
                .filter(t -> !this.parentHasDistantSousType(typeCode, t.getCode()) && !t.getCode().equals(typeCode))
                .collect(Collectors.toList());
        result.forEach(dto -> dto.setParentTypeCodes(typeRepo.getParentTypeCodes(dto.getCode())));
        return result;
    }

    @Override
    public List<TypeDTO> getTypesByGroupCode(String groupCode)
    {
        List<TypeDTO> result;
        if ("STR".equals(groupCode)) //Si le groupCode est STR, il ne faut retourner que les types que l'utilisateur connecté est en capacité de voir
        {
            Long userProfileStrId = jwtService.getCurrentUserProfileStrId();
            if (userProfileStrId != null)
            {
                VStructure structure = vStrRepo.findById(userProfileStrId).orElseThrow(()->new AppException("Structure introuvable : " + userProfileStrId));
                if (structure != null)
                {
                    String typeCode = structure.getStrTypeCode();
                    result = getPossibleSousTypes(typeCode);
                    return result;
                }
            }
        }
        result = typeRepo.findByGroupCode(groupCode);
        result.forEach(dto -> dto.setParentTypeCodes(typeRepo.getParentTypeCodes(dto.getCode())));
        return result;
    }

    @Override
    public List<TypeGroupDTO> getAllTypeGroups()
    {
        return typeGroupRepo.getAllTypeGroupes();
    }
}
