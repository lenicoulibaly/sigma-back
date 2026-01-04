package lenicorp.admin.workflowengine.model.dtos.mapper;

import lenicorp.admin.workflowengine.model.dtos.TransitionRuleDTO;
import lenicorp.admin.workflowengine.model.entities.Transition;
import lenicorp.admin.workflowengine.model.entities.TransitionRule;
import lenicorp.admin.types.model.entities.Type;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransitionRuleMapper
{

    @Mapping(target = "transitionId", source = "transition.transitionId")
    @Mapping(target = "transitionPrivilegeCode", source = "transition.privilege.code")
    @Mapping(target = "statutDestinationCode", source = "statutDestination.code")
    TransitionRuleDTO toDto(TransitionRule entity);

    @Mapping(target = "transition", source = "transitionId")
    @Mapping(target = "statutDestination", source = "statutDestinationCode")
    TransitionRule toEntity(TransitionRuleDTO dto);

    default Transition mapTransition(Long transitionId) {
        if (transitionId == null) return null;
        Transition t = new Transition();
        t.setTransitionId(transitionId);
        return t;
    }

    default Type mapType(String code) {
        return code == null || code.isBlank() ? null : new Type(code);
    }
}
