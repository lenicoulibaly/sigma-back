package lenicorp.metier.association.controller.resources;

import jakarta.validation.Valid;
import lenicorp.admin.utilities.validatorgroups.CreateGroup;
import lenicorp.admin.utilities.validatorgroups.UpdateGroup;
import lenicorp.metier.association.controller.services.IAssociationService;
import lenicorp.metier.association.model.dtos.CreateAssociationDTO;
import lenicorp.metier.association.model.dtos.ReadAssociationDTO;
import lenicorp.metier.association.model.dtos.UpdateAssociationDTO;
import lenicorp.metier.association.model.entities.Association;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@RestController @RequestMapping("/associations") @RequiredArgsConstructor
@Validated
public class AssociationController
{
    private final IAssociationService associationService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Validated(CreateGroup.class)
    public Association createAssociation(@Valid @ModelAttribute CreateAssociationDTO dto) throws IOException
    {
        return associationService.createAssociation(dto);
    }

    @PutMapping("/update") @Validated(UpdateGroup.class)
    public Association updateAssociation(@Valid @RequestBody UpdateAssociationDTO dto)
    {
        return associationService.updateAssociation(dto);
    }

    @GetMapping("/search")
    public Page<ReadAssociationDTO> searchAssociations(@RequestParam(value = "key", defaultValue = "", required = false) @DefaultValue("") String key,
                                                       //@RequestParam("strId") Long strId,
                                                       @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                       @RequestParam(value = "size", defaultValue = "0", required = false) int size)
    {
        return associationService.searchAssociations(key, PageRequest.of(page, size));
    }

    @GetMapping("/open/search-list")
    public List<ReadAssociationDTO> searchAssociationsList(@RequestParam(value = "key", defaultValue = "", required = false) @DefaultValue("") String key)
    {
        return associationService.searchAssociationsList(key);
    }

    @GetMapping("/find-by-id/{assoId}")
    public ReadAssociationDTO findById(@PathVariable("assoId") Long assoId)
    {
        return associationService.findById(assoId);
    }

    @GetMapping("/generate-fiche-adhesion/{assoId}")
    public String generateFicheAdhesion(@PathVariable("assoId") Long assoId) throws Exception
    {
        byte[] bytes = associationService.generateFicheAdhesion(assoId);
        String base64String = Base64.getEncoder().encodeToString(bytes);
        return base64String;
    }
}
