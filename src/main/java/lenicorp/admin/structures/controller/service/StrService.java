package lenicorp.admin.structures.controller.service;

import lenicorp.admin.security.controller.services.specs.IJwtService;
import lenicorp.admin.structures.controller.repositories.StrRepo;
import lenicorp.admin.structures.controller.repositories.VStrRepo;
import lenicorp.admin.structures.model.dtos.ChangeAnchorDTO;
import lenicorp.admin.structures.model.dtos.CreateOrUpdateStrDTO;
import lenicorp.admin.structures.model.dtos.ReadStrDTO;
import lenicorp.admin.structures.model.dtos.StrMapper;
import lenicorp.admin.structures.model.entities.Structure;
import lenicorp.admin.structures.model.entities.VStructure;
import lenicorp.admin.utilities.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component @RequiredArgsConstructor
public class StrService implements IStrService
{
    private final VStrRepo vsRepo;
    private final StrRepo strRepo;
    private final StrMapper strMapper;
    private final IJwtService jwtService;
    @PersistenceContext
    private EntityManager entityManager;

    @Override @Transactional
    public ReadStrDTO createStr(CreateOrUpdateStrDTO dto)
    {
        Structure str = strMapper.mapToStructureForCreate(dto);
        str = strRepo.save(str);
        return strMapper.mapToReadStrDTO(str);
    }

    @Override
    @Transactional
    public ReadStrDTO updateStr(CreateOrUpdateStrDTO dto)
    {
        Structure str = strRepo.findById(dto.getStrId()).orElseThrow();
        str = strMapper.updateStructureFromDTO(dto, str);
        str = strRepo.save(str);
        return strMapper.mapToReadStrDTO(str);
    }

    @Override @Transactional //TODO à implémenter et à valider
    public ReadStrDTO changeAnchor(ChangeAnchorDTO dto)
    {
        Structure str = strRepo.findById(dto.getStrId()).orElseThrow();
        str = strMapper.updateParentFromChangeAnchorDto(dto, str);
        str = strRepo.save(str);
        return strMapper.mapToReadStrDTO(str);
    }

    @Override
    public Page<ReadStrDTO> searchStrs(String key, String typeCode, PageRequest pageRequest)
    {
        Long profileStrId = jwtService.getCurrentUserProfile() != null ? jwtService.getCurrentUserProfile().getAssStrId() : null;
        return this.search(key, profileStrId, typeCode, pageRequest);
    }

    @Override
    public List<ReadStrDTO> getPossibleParentStructures(String childTypeCode)
    {
        // Get the current user profile
        var userProfile = jwtService.getCurrentUserProfile();
        // Get the list of possible parent structures
        List<ReadStrDTO> possibleParents = strRepo.getPossibleParentStructures(childTypeCode);

        // If user profile is null or profileStrChaineSigles is null, return all possible parents
        if (userProfile == null || userProfile.getAssStrChaineSigles() == null) {
            return possibleParents;
        }

        // Get the user's chain of sigles
        String userChaineSigles = userProfile.getAssStrChaineSigles();

        // Filter the structures based on the user's chain of sigles
        return possibleParents.stream()
                .filter(str -> str.getChaineSigles() != null && 
                              (str.getChaineSigles().equals(userChaineSigles) || 
                               str.getChaineSigles().startsWith(userChaineSigles + "/")))
                .toList();
    }

    @Override
    public List<ReadStrDTO> getRootStructures()
    {
        return strRepo.getRootStructures();
    }

    @Override
    public CreateOrUpdateStrDTO getUpdateDto(Long strId)
    {
        return strRepo.getUpdateDto(strId);
    }

    @Override
    public ChangeAnchorDTO getChangeAnchorDto(Long strId)
    {
        return strRepo.getChangeAnchorDto(strId);
    }

    @Override
    public List<ReadStrDTO> getUserVisibleStructures()
    {
        Long profileStrId = jwtService.getCurrentUserProfile() != null ? jwtService.getCurrentUserProfile().getAssStrId() : null;
        return this.findAllDescendants(profileStrId);
    }

    @Override
    public List<ReadStrDTO> findAllDescendants(Long strId) {
        String parentChaineSigles = vsRepo.getChaineSigles(strId);

        if (parentChaineSigles == null) return Collections.emptyList();

        // Deuxième requête pour trouver tous les descendants
        String jpql = "SELECT vs FROM VStructure vs WHERE vs.chaineSigles LIKE :chaineSiglesPattern OR vs.strId = :strId ORDER BY vs.chaineSigles";
        List<VStructure> descendants = entityManager.createQuery(jpql, VStructure.class)
                .setParameter("chaineSiglesPattern", parentChaineSigles + "/%")
                .setParameter("strId", strId)
                .getResultList();

        return strMapper.mapToReadStrDTOList(descendants);
    }

    @Override
    public Page<ReadStrDTO> search(String key, Long parentId, String typeCode, PageRequest pageRequest)
    {
        String safeKey = "%" + StringUtils.stripAccentsToUpperCase(key) + "%";
        int page = pageRequest.getPageNumber();
        int size = pageRequest.getPageSize();
        String parentChaineSigles = parentId == null ? null : vsRepo.getChaineSigles(parentId);
        typeCode = typeCode == null ? null : typeCode.equals("") ? null : typeCode;
        String baseQuery = """
                from VStructure vs 
                where 
                    vs.strTypeCode = coalesce(:typeCode, vs.strTypeCode) 
                    and (vs.strName like :key or vs.strSigle like :key)
                """;
        if (parentChaineSigles != null) baseQuery += " and vs.chaineSigles like :parentChaineSigles";

        String countQuery = "select count(vs.strId) " + baseQuery;
        String selectQuery = "select vs " + baseQuery + " order by vs.chaineSigles";

        var countQueryExecutor = entityManager.createQuery(countQuery, Long.class)
                .setParameter("key", safeKey)
                .setParameter("typeCode", typeCode);
        if (parentChaineSigles != null) countQueryExecutor.setParameter("parentChaineSigles", parentChaineSigles + "%");
        Long totalElements = countQueryExecutor.getSingleResult();

        var selectQueryExecutor = entityManager.createQuery(selectQuery, VStructure.class)
                .setParameter("key", safeKey)
                .setParameter("typeCode", typeCode);

        if (parentChaineSigles != null)
            selectQueryExecutor.setParameter("parentChaineSigles", parentChaineSigles + "%");
        List<VStructure> content = selectQueryExecutor.setMaxResults(size).setFirstResult(page * size).getResultList();

        List<ReadStrDTO> readStrDTOList = strMapper.mapToReadStrDTOList(content);

        return new PageImpl<>(readStrDTOList, pageRequest, totalElements);
    }
}
