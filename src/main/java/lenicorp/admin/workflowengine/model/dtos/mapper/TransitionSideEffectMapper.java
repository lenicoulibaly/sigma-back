package lenicorp.admin.workflowengine.model.dtos.mapper;

import lenicorp.admin.workflowengine.model.dtos.TransitionSideEffectDTO;
import lenicorp.admin.workflowengine.model.entities.Transition;
import lenicorp.admin.workflowengine.model.entities.TransitionSideEffect;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransitionSideEffectMapper {

    @Mapping(target = "transitionId", source = "transition.transitionId")
    TransitionSideEffectDTO toDto(TransitionSideEffect entity);

    @Mapping(target = "transition", source = "transitionId")
    TransitionSideEffect toEntity(TransitionSideEffectDTO dto);

    @Mapping(target = "transition", source = "transitionId")
    void updateEntity(TransitionSideEffectDTO dto, @MappingTarget TransitionSideEffect entity);

    default Transition mapTransition(Long id) {
        if (id == null) return null;
        Transition t = new Transition();
        t.setTransitionId(id);
        return t;
    }
}
