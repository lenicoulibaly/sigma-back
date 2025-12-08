package lenicorp.admin.workflowengine.admin.mapper;

import lenicorp.admin.workflowengine.admin.dto.TransitionRuleAdminDTO;
import lenicorp.admin.workflowengine.model.entities.Transition;
import lenicorp.admin.workflowengine.model.entities.TransitionRule;
import lenicorp.admin.types.model.entities.Type;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransitionRuleAdminMapper {

    @Mapping(target = "transitionPrivilegeCode", source = "transition.privilegeCode")
    @Mapping(target = "statutDestinationCode", source = "statutDestination.code")
    TransitionRuleAdminDTO toDto(TransitionRule entity);

    @Mapping(target = "transition", source = "transitionPrivilegeCode")
    @Mapping(target = "statutDestination", source = "statutDestinationCode")
    TransitionRule toEntity(TransitionRuleAdminDTO dto);

    default Transition mapTransition(String privilegeCode) {
        if (privilegeCode == null || privilegeCode.isBlank()) return null;
        Transition t = new Transition();
        t.setPrivilegeCode(privilegeCode);
        return t;
    }

    default Type mapType(String code) {
        return code == null || code.isBlank() ? null : new Type(code);
    }
}
