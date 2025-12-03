package lenicorp.metier.association.model.dtos;

import jakarta.validation.constraints.AssertTrue;
import lenicorp.admin.archive.model.dtos.request.UploadDocReq;

import java.util.List;

public record CreateDemandeAdhesionDTO(
        Long assoId,
        Long sectionId,
        String message,
        @AssertTrue(message = "Vous devez accepter le RGPD") boolean accepteRgpd,
        @AssertTrue(message = "Vous devez accepter la charte d'adhésion") boolean accepteCharte,
        List<UploadDocReq> documents
) {}
