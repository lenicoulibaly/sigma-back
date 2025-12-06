package lenicorp.metier.association.model.mappers;

import lenicorp.admin.security.controller.services.specs.IJwtService;
import lenicorp.admin.security.model.entities.AppUser;
import lenicorp.metier.association.model.dtos.CreateDemandeAdhesionDTO;
import lenicorp.metier.association.model.dtos.ReadDemandeAdhesionDTO;
import lenicorp.metier.association.model.entities.DemandeAdhesion;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DemandeAdhesionMapper {

    // Record(Create) -> Entity
    @Mapping(target = "association", expression = "java(dto.assoId() == null ? null : new Association(dto.assoId()))")
    @Mapping(target = "section", expression = "java(dto.sectionId() == null ? null : new Section(dto.sectionId()))")
    @Mapping(target = "demandeur", expression = "java(jwtService.getCurrentUser())")
    @Mapping(target = "dateSoumission", expression = "java(java.time.LocalDateTime.now())")
    // statut & reference & dateDecision & adhesionCreee managed in service
    DemandeAdhesion mapTopDemandeAdhesion(CreateDemandeAdhesionDTO dto, @Context IJwtService jwtService);

    // Entity -> Record(Read)
    @Mapping(target = "assoId", source = "association.assoId")
    @Mapping(target = "sectionId", source = "section.sectionId")
    @Mapping(target = "userId", source = "demandeur.userId")
    @Mapping(target = "userFullName", expression = "java(combineUserName(entity.getDemandeur()))")
    @Mapping(target = "statutCode", source = "statut.code")
    @Mapping(target = "statutName", source = "statut.name")
    @Mapping(target = "montantDu", source = "montantCotisationEstime")
    @Mapping(target = "adhesionIdCreee", source = "adhesionCreee.adhesionId")
    @Mapping(target = "decideurFullName", expression = "java(null)")
    ReadDemandeAdhesionDTO mapTopDemandeAdhesionReadDTO(DemandeAdhesion entity);

    // Helper
    default String combineUserName(AppUser u) {
        if (u == null) return null;
        String fn = u.getFirstName() == null ? "" : u.getFirstName();
        String ln = u.getLastName() == null ? "" : u.getLastName();
        String full = (fn + " " + ln).trim();
        return full.isEmpty() ? null : full;
    }
}
