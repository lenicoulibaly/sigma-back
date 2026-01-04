package lenicorp.admin.workflowengine.model.dtos.mapper;

import lenicorp.admin.security.model.entities.AppAuthority;
import lenicorp.admin.workflowengine.model.dtos.SideEffectDTO;
import lenicorp.admin.workflowengine.model.dtos.TransitionDTO;
import lenicorp.admin.workflowengine.model.entities.Transition;
import lenicorp.admin.workflowengine.model.entities.TransitionSideEffect;
import lenicorp.admin.workflowengine.model.entities.Workflow;
import lenicorp.admin.types.model.entities.Type;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransitionMapper
{

    @Mappings({
            @Mapping(target = "privilegeCode", source = "privilege.code"),
            @Mapping(target = "statutOrigineCode", source = "statutOrigine.code"),
            @Mapping(target = "statutOrigineName", source = "statutOrigine.name"),
            @Mapping(target = "defaultStatutDestinationCode", source = "defaultStatutDestination.code"),
            @Mapping(target = "defaultStatutDestinationName", source = "defaultStatutDestination.name"),
            @Mapping(target = "workflowId", source = "workflow.id"),
            @Mapping(target = "commentRequired", source = "validationConfig.commentRequired"),
            @Mapping(target = "requiredDocTypeCodes", expression = "java(toCodes(entity.getValidationConfig() == null ? null : entity.getValidationConfig().getRequiredDocTypes()))"),
            @Mapping(target = "sideEffects", source = "sideEffects")
    })
    TransitionDTO toDto(Transition entity);

    @Mappings({
            @Mapping(target = "privilege", source = "privilegeCode"),
            @Mapping(target = "statutOrigine", source = "statutOrigineCode"),
            @Mapping(target = "defaultStatutDestination", source = "defaultStatutDestinationCode"),
            @Mapping(target = "workflow", source = "workflowId"),
            @Mapping(target = "validationConfig.commentRequired", source = "commentRequired"),
            @Mapping(target = "validationConfig.requiredDocTypes", ignore = true),
            @Mapping(target = "sideEffects", ignore = true),
            @Mapping(target = "rules", ignore = true)
    })
    Transition toEntity(TransitionDTO dto);

    @Mappings({
            @Mapping(target = "privilege", source = "privilegeCode"),
            @Mapping(target = "statutOrigine", source = "statutOrigineCode"),
            @Mapping(target = "defaultStatutDestination", source = "defaultStatutDestinationCode"),
            @Mapping(target = "workflow", source = "workflowId"),
            @Mapping(target = "validationConfig.commentRequired", source = "commentRequired"),
            @Mapping(target = "validationConfig.requiredDocTypes", ignore = true),
            @Mapping(target = "sideEffects", ignore = true),
            @Mapping(target = "rules", ignore = true)
    })
    void updateEntity(TransitionDTO dto, @MappingTarget Transition entity);

    SideEffectDTO sideEffectToSideEffectDTO(TransitionSideEffect sideEffect);
    TransitionSideEffect sideEffectDTOToTransitionSideEffect(SideEffectDTO sideEffectDTO);

    @AfterMapping
    default void handleCollections(TransitionDTO dto, @MappingTarget Transition entity) {
        if (dto.getSideEffects() != null) {
            if (entity.getSideEffects() == null) {
                entity.setSideEffects(new java.util.ArrayList<>());
            }
            entity.getSideEffects().clear();
            for (SideEffectDTO seDto : dto.getSideEffects()) {
                TransitionSideEffect se = sideEffectDTOToTransitionSideEffect(seDto);
                se.setTransition(entity);
                entity.getSideEffects().add(se);
            }
        } else if (entity.getSideEffects() != null) {
            entity.getSideEffects().clear();
        }
    }

    default java.util.List<String> toCodes(java.util.List<Type> types) {
        return types == null ? java.util.List.of() : types.stream().map(t -> t.code).toList();
    }

    default String mapTypeToString(Type type) {
        return type == null ? null : type.code;
    }

    default Type mapType(String code) {
        return code == null || code.isBlank() ? null : new Type(code);
    }

    default AppAuthority mapAuthority(String code) {
        return code == null || code.isBlank() ? null : new AppAuthority(code);
    }

    default Workflow mapWorkflow(Long id) {
        if (id == null) return null;
        Workflow w = new Workflow();
        w.setId(id);
        return w;
    }
}
