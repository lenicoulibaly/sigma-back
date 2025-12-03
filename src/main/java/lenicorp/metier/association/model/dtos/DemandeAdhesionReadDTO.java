package lenicorp.metier.association.model.dtos;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record DemandeAdhesionReadDTO(
        Long demandeId,
        String reference,
        Long assoId,
        Long sectionId,
        Long userId,
        String userFullName,
        String statutCode,
        String statutName,
        String message,
        LocalDateTime dateSoumission,
        LocalDateTime dateDecision,
        String decideurFullName,
        BigDecimal montantDu,
        Long adhesionIdCreee
) {}
