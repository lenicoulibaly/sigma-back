package lenicorp.metier.association.controller.resources;

import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lenicorp.admin.utilities.SelectOption;
import lenicorp.admin.utilities.validatorgroups.CreateGroup;
import lenicorp.admin.utilities.validatorgroups.UpdateGroup;
import lenicorp.metier.association.controller.services.IAdhesionService;
import lenicorp.metier.association.model.dtos.AdhesionDTO;
import lenicorp.metier.association.model.entities.Adhesion;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequestMapping("/adhesions") @Validated @RequiredArgsConstructor
public class AdhesionController
{
    private final IAdhesionService adhesionService;

    @PostMapping("/create")
    @Validated(CreateGroup.class)
    public Adhesion createAdhesion(@Valid @RequestBody AdhesionDTO dto)
    {
        return adhesionService.createUserAndAdhesion(dto);
    }

    @PutMapping("/update")
    @Validated(UpdateGroup.class)
    public Adhesion updateAdhesion(@Valid @RequestBody AdhesionDTO dto)
    {
        return adhesionService.updateMembre(dto);
    }

    @PutMapping("/desabonner/{adhesionId}")
    public void desister(@PathParam("adhesionId") Long adhesionId)
    {
        adhesionService.seDesabonner(adhesionId);
    }

    @GetMapping("/search-members")
    public Page<AdhesionDTO> searchMembers(@RequestParam(value = "key", required = false, defaultValue = "") String key,
                                           @RequestParam("assoId") Long assoId,
                                           @RequestParam("sectionId") Long sectionId,
                                           @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                           @RequestParam(value = "size", required = false, defaultValue = "10") int size)
    {
        return adhesionService.searchAdhsions(key, assoId, sectionId, PageRequest.of(page, size));
    }

    @GetMapping("/all-options")
    public List<SelectOption> getAdhesionOptions(@RequestParam("assoId") Long assoId)
    {
        return adhesionService.getOptions(assoId);
    }

    @GetMapping("/get-membre-dto")
    public AdhesionDTO getMembreDto(@RequestParam("username") String username)
    {
        return adhesionService.getMembreDTO(username);
    }
}
