package lenicorp.metier.association.controller.resources;

import jakarta.validation.Valid;
import lenicorp.admin.utilities.validatorgroups.CreateGroup;
import lenicorp.admin.utilities.validatorgroups.UpdateGroup;
import lenicorp.metier.association.controller.services.ISectionService;
import lenicorp.metier.association.model.dtos.CreateSectionDTO;
import lenicorp.metier.association.model.dtos.ReadSectionDTO;
import lenicorp.metier.association.model.dtos.UpdateSectionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/sections") @RequiredArgsConstructor @Validated
public class SectionController
{
    private final ISectionService sectionService;

    @PostMapping("/create")
    @Validated(CreateGroup.class)
    public ReadSectionDTO createSection(@Valid @RequestBody CreateSectionDTO dto)
    {
        return sectionService.createSection(dto);
    }

    @PutMapping("/update")
    @Validated(UpdateGroup.class)
    public ReadSectionDTO updateSection(@Valid @RequestBody UpdateSectionDTO dto)
    {
        return sectionService.updateSection(dto);
    }

    @GetMapping("/search")
    public Page<ReadSectionDTO> searchSections(@RequestParam(value = "key", defaultValue = "", required = false) String key,
                                        @RequestParam(value = "assoId", required = false) Long assoId,
                                        @RequestParam(value = "strId", required = false) Long strId,
                                        @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                        @RequestParam(value = "size", defaultValue = "10", required = false) int size)
    {
        return sectionService.searchSections(key, assoId, strId, PageRequest.of(page, size));
    }

    @GetMapping("/find-by-asso/{assoId}")
    public List<ReadSectionDTO> getAssociationSections(@PathVariable("assoId") Long assoId)
    {
        return sectionService.getAssociationSections(assoId);
    }
}

/**
 * @PostMapping("/create") public ReadSectionDTO createSection(@RequestBody CreateSectionDTO dto)
 * @PutMapping("/update") public ReadSectionDTO updateSection(@RequestBody UpdateSectionDTO dto)
 *
 * @GetMapping("/search") public Page<ReadSectionDTO> searchSections(@RequestParam(value = "key", defaultValue = "", required = false) String key,
 *                                         @RequestParam(value = "assoId", required = false) Long assoId,
 *                                         @RequestParam(value = "strId", required = false) Long strId,
 *                                         @RequestParam(value = "page", defaultValue = "0", required = false) int page,
 *                                         @RequestParam(value = "size", defaultValue = "10", required = false) int size)
 * @GetMapping("/find-by-asso/{assoId}") public List<ReadSectionDTO> getAssociationSections(@PathVariable("assoId") Long assoId)
 */