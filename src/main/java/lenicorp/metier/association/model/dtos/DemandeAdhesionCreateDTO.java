package lenicorp.metier.association.model.dtos;

public record DemandeAdhesionCreateDTO(
        Long assoId,
        Long sectionId,
        String message,
        boolean accepteRgpd,
        boolean accepteCharte
) {}
