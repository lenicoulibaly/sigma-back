package lenicorp.metier.association.controller.resources;

import lenicorp.metier.association.controller.services.DemandeAdhesionService;
import lenicorp.metier.association.model.dtos.DemandeAdhesionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/demandes-adhesion")
@RequiredArgsConstructor
public class DemandeAdhesionController
{
    private final DemandeAdhesionService service;

    @PostMapping
    public DemandeAdhesionDTO createSimple(@RequestBody DemandeAdhesionDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public DemandeAdhesionDTO updateSimple(@PathVariable("id") Long id, @RequestBody DemandeAdhesionDTO dto) {
        return service.update(id, dto);
    }

    @GetMapping("/search")
    public Page<DemandeAdhesionDTO> searchSimple(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "statusGroupCode", required = false) String statusGroupCode,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        return service.search(key, statusGroupCode, PageRequest.of(page, size));
    }
}
