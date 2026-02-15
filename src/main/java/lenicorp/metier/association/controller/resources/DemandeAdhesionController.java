package lenicorp.metier.association.controller.resources;

import jakarta.validation.Valid;
import lenicorp.admin.security.controller.services.specs.IJwtService;
import lenicorp.metier.association.controller.services.DemandeAdhesionService;
import lenicorp.metier.association.model.dtos.DemandeAdhesionDTO;
import lenicorp.metier.association.model.dtos.UserDemandeAdhesionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/demandes-adhesion")
@RequiredArgsConstructor
public class DemandeAdhesionController
{
    private final DemandeAdhesionService service;
    private final IJwtService jwtService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DemandeAdhesionDTO create(@ModelAttribute DemandeAdhesionDTO dto) {
        return service.create(dto);
    }

    @PostMapping(value = "/open/user-demande", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DemandeAdhesionDTO createUserAndDemandeAdhesion(@Valid @ModelAttribute UserDemandeAdhesionDTO dto) {
        return service.createUserAndDemandeAdhesion(dto);
    }

    @PutMapping("/{id}")
    @PutMapping(value="/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DemandeAdhesionDTO update(@PathVariable("id") Long id, @Valid DemandeAdhesionDTO dto) {
        return service.update(id, dto);
    }

    @GetMapping("/{id}")
    public DemandeAdhesionDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @GetMapping({"/search", "/{assoId}/search"})
    public Page<DemandeAdhesionDTO> search(
            @PathVariable(value = "assoId", required = false) Long assoId,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "statusGroupCode", required = false) String statusGroupCode,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        Long userId = "BROUIL".equals(statusGroupCode) ? jwtService.getCurrentUser().getUserId() : null;
        return service.search(assoId, userId, key, statusGroupCode, PageRequest.of(page, size));
    }

    @GetMapping("/user-demandes")
    public Page<DemandeAdhesionDTO> searchForUser(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "assoIds", required = false) List<Long> assoIds,
            @RequestParam(value = "workflowStatusGroupCodes", required = false) List<String> workflowStatusGroupCodes,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        return service.searchForUser(userId, key, assoIds, workflowStatusGroupCodes, PageRequest.of(page, size));
    }
}