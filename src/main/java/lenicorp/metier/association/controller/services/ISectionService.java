package lenicorp.metier.association.controller.services;

import lenicorp.metier.association.model.dtos.CreateSectionDTO;
import lenicorp.metier.association.model.dtos.ReadSectionDTO;
import lenicorp.metier.association.model.dtos.UpdateSectionDTO;
import lenicorp.metier.association.model.entities.Association;
import lenicorp.metier.association.model.entities.Section;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ISectionService
{
    ReadSectionDTO createSection(CreateSectionDTO dto);

    ReadSectionDTO updateSection(UpdateSectionDTO dto);

    Page<ReadSectionDTO> searchSections(String key, Long assoId, Long strId, Pageable pageable);

    Section createSectionDeBase(Association association);

    List<ReadSectionDTO> getAssociationSections(Long assoId);
}
