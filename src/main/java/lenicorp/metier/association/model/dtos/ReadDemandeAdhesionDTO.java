package lenicorp.metier.association.model.dtos;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReadDemandeAdhesionDTO(
        Long demandeId,
        Long assoId,
        String assoName,
        Long sectionId,
        Long userId,
        String userFullName,
        String statutCode,
        String statutName,
        String statutColor,
        String statutIcon,
        String message,
        LocalDateTime dateSoumission,
        LocalDateTime dateDecision,
        LocalDateTime createdAt,
        String decideurFullName,
        Long adhesionIdCreee
) {}
