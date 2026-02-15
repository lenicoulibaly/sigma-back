package lenicorp.metier.association.model.mappers;

import lenicorp.admin.workflowengine.controller.repositories.WorkflowStatusRepository;
import lenicorp.admin.workflowengine.model.entities.WorkflowStatus;
import lenicorp.metier.association.model.dtos.CreateDemandeAdhesionDTO;
import lenicorp.metier.association.model.entities.DemandeAdhesion;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class DemandeAdhesionMapper {

    @Autowired
    protected WorkflowStatusRepository workflowStatusRepository;

    // Record(Create) -> Entity
    @Mapping(target = "association", expression = "java(dto.assoId() == null ? null : new lenicorp.metier.association.model.entities.Association(dto.assoId()))")
    @Mapping(target = "section", expression = "java(dto.sectionId() == null ? null : new lenicorp.metier.association.model.entities.Section(dto.sectionId()))")
    @Mapping(target = "demandeur", expression = "java(jwtService.getCurrentUser())")
    @Mapping(target = "dateSoumission", expression = "java(java.time.LocalDateTime.now())")
    // statut & reference & dateDecision & adhesionCreee managed in service
    public abstract DemandeAdhesion mapTopDemandeAdhesion(CreateDemandeAdhesionDTO dto, @Context lenicorp.admin.security.controller.services.specs.IJwtService jwtService);

    @Mapping(target = "association", source = "assoId", qualifiedByName = "idToAssociation")
    @Mapping(target = "section", source = "sectionId", qualifiedByName = "idToSection")
    @Mapping(target = "demandeur", source = "demandeurId", qualifiedByName = "idToUser")
    public abstract DemandeAdhesion toEntity(lenicorp.metier.association.model.dtos.DemandeAdhesionDTO dto);

    @Mapping(target = "assoId", source = "association.assoId")
    @Mapping(target = "assoName", source = "association.assoName")
    @Mapping(target = "sectionId", source = "section.sectionId")
    @Mapping(target = "sectionName", source = "section.sectionName")
    @Mapping(target = "demandeurId", source = "demandeur.userId")
    @Mapping(target = "demandeurNom", expression = "java(combineUserName(entity.getDemandeur()))")
    @Mapping(target = "statutCode", source = "statut.code")
    @Mapping(target = "statutNom", source = "statut.name")
    @Mapping(target = "statutColor", expression = "java(getStatutColor(entity))")
    @Mapping(target = "statutIcon", expression = "java(getStatutIcon(entity))")
    @Mapping(target = "createdAt", source = "createdAt")
    public abstract lenicorp.metier.association.model.dtos.DemandeAdhesionDTO toDto(DemandeAdhesion entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "demandeId", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "association", ignore = true)
    @Mapping(target = "section", ignore = true)
    @Mapping(target = "demandeur", ignore = true)
    public abstract void updateEntity(lenicorp.metier.association.model.dtos.DemandeAdhesionDTO dto, @MappingTarget DemandeAdhesion entity);

    @Named("idToAssociation")
    protected lenicorp.metier.association.model.entities.Association idToAssociation(Long id) {
        if (id == null) return null;
        return new lenicorp.metier.association.model.entities.Association(id);
    }

    @Named("idToSection")
    protected lenicorp.metier.association.model.entities.Section idToSection(Long id) {
        if (id == null) return null;
        return new lenicorp.metier.association.model.entities.Section(id);
    }

    @Named("idToUser")
    protected lenicorp.admin.security.model.entities.AppUser idToUser(Long id) {
        if (id == null) return null;
        return new lenicorp.admin.security.model.entities.AppUser(id);
    }

    // Helper
    protected String combineUserName(lenicorp.admin.security.model.entities.AppUser u) {
        if (u == null) return null;
        String fn = u.getFirstName() == null ? "" : u.getFirstName();
        String ln = u.getLastName() == null ? "" : u.getLastName();
        String full = (fn + " " + ln).trim();
        return full.isEmpty() ? null : full;
    }

    protected String getStatutColor(DemandeAdhesion entity) {
        if (entity.getStatut() == null) return null;
        return workflowStatusRepository.findByWorkflowCodeAndStatusCode("DMD_ADH", entity.getStatut().code)
                .map(WorkflowStatus::getColor)
                .orElse(null);
    }

    protected String getStatutIcon(DemandeAdhesion entity) {
        if (entity.getStatut() == null) return null;
        return workflowStatusRepository.findByWorkflowCodeAndStatusCode("DMD_ADH", entity.getStatut().code)
                .map(WorkflowStatus::getIcon)
                .orElse(null);
    }
}
