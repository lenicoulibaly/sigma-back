package lenicorp.metier.association.controller.resources;

import jakarta.validation.Valid;
import lenicorp.metier.association.controller.services.IDemandeAdhesionService;
import lenicorp.metier.association.model.dtos.DemandeAdhesionCreateDTO;
import lenicorp.metier.association.model.dtos.DemandeAdhesionReadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/demandes-adhesion")
@RequiredArgsConstructor
public class DemandeAdhesionController {

    private final IDemandeAdhesionService service;

    // Création d'une demande d'adhésion -> EN_ATTENTE
    @PostMapping
    public DemandeAdhesionReadDTO create(@Valid @RequestBody DemandeAdhesionCreateDTO dto) {
        return service.create(dto);
    }

    // Prendre en étude une demande
    @PutMapping("/{id}/prendre-en-etude")
    public DemandeAdhesionReadDTO prendreEnEtude(@PathVariable("id") Long demandeId) {
        return service.prendreEnEtude(demandeId);
    }

    // Approuver: vers APPROUVEE ou EN_ATTENTE_PAIEMENT selon droit d'adhésion
    @PutMapping("/{id}/approuver")
    public DemandeAdhesionReadDTO approuver(@PathVariable("id") Long demandeId) {
        return service.approuver(demandeId);
    }

    // Confirmation de paiement: EN_ATTENTE_PAIEMENT -> APPROUVEE + création Adhesion
    @PutMapping("/{id}/confirmer-paiement")
    public DemandeAdhesionReadDTO confirmerPaiement(@PathVariable("id") Long demandeId) {
        return service.confirmerPaiement(demandeId);
    }

    // Rejeter une demande (motif obligatoire)
    @PutMapping("/{id}/rejeter")
    public DemandeAdhesionReadDTO rejeter(@PathVariable("id") Long demandeId,
                                          @RequestParam("motifRefus") String motifRefus) {
        return service.rejeter(demandeId, motifRefus);
    }

    // Annuler une demande (non terminale)
    @PutMapping("/{id}/annuler")
    public DemandeAdhesionReadDTO annuler(@PathVariable("id") Long demandeId) {
        return service.annuler(demandeId);
    }

    // Recherche paginée triée du plus ancien au plus récent
    @GetMapping
    public Page<DemandeAdhesionReadDTO> search(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "assoId", required = false) Long assoId,
            @RequestParam(value = "statutCodes", required = false) List<String> statutCodes,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size
    ) {
        return service.search(key, assoId, statutCodes, PageRequest.of(page, size));
    }
}
