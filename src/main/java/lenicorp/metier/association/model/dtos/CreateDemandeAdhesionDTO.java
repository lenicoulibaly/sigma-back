package lenicorp.metier.association.model.dtos;

import jakarta.validation.constraints.AssertTrue;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;

import java.util.List;

public record CreateDemandeAdhesionDTO(
        Long assoId,
        Long sectionId,
        String message,
        @AssertTrue(message = "Vous devez accepter le RGPD") boolean accepteRgpd,
        // La charte est requise uniquement si la charte a été chargée pour l'association
        boolean accepteCharte,
        // Les statuts/règlements sont requis uniquement si un document existe pour l'association
        boolean accepteStatutsReglements,
        List<UploadDocReq> documents
) {}
