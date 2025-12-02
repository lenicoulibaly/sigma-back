package lenicorp.admin.structures.controller.service;

import lenicorp.admin.structures.model.dtos.ChangeAnchorDTO;
import lenicorp.admin.structures.model.dtos.CreateOrUpdateStrDTO;
import lenicorp.admin.structures.model.dtos.ReadStrDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IStrService
{
    ReadStrDTO createStr(CreateOrUpdateStrDTO dto);

    ReadStrDTO updateStr(CreateOrUpdateStrDTO dto);

    ReadStrDTO changeAnchor(ChangeAnchorDTO dto);

    Page<ReadStrDTO> searchStrs(String key, String typeCode, PageRequest pageRequest);

    List<ReadStrDTO> getPossibleParentStructures(String childTypeCode);
    List<ReadStrDTO> getRootStructures();
    CreateOrUpdateStrDTO getUpdateDto(Long strId);

    ChangeAnchorDTO getChangeAnchorDto(Long strId);

    /**
     * Returns all structures under supervision of the structure with the given ID,
     * including all levels of the hierarchy down to the lowest level.
     * @param parentStrId The ID of the parent structure
     * @return List of structures under supervision
     */
    List<ReadStrDTO> findAllDescendants(Long parentStrId);

    List<ReadStrDTO> getUserVisibleStructures();
    List<ReadStrDTO> searchStrList(String key, Long parentId, String typeCode);
    Page<ReadStrDTO> search(String key, Long parentId, String typeCode, Pageable pageable);
}
