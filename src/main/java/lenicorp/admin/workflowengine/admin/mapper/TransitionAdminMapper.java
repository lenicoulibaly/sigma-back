package lenicorp.admin.workflowengine.admin.mapper;

import lenicorp.admin.workflowengine.admin.dto.TransitionAdminDTO;
import lenicorp.admin.workflowengine.model.entities.Transition;
import lenicorp.admin.workflowengine.model.entities.Workflow;
import lenicorp.admin.types.model.entities.Type;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TransitionAdminMapper {

    @Mappings({
            @Mapping(target = "statutOrigineCode", source = "statutOrigine.code"),
            @Mapping(target = "defaultStatutDestinationCode", source = "defaultStatutDestination.code"),
            @Mapping(target = "workflowId", source = "workflow.id")
    })
    TransitionAdminDTO toDto(Transition entity);

    @Mappings({
            @Mapping(target = "statutOrigine", source = "statutOrigineCode"),
            @Mapping(target = "defaultStatutDestination", source = "defaultStatutDestinationCode"),
            @Mapping(target = "workflow", source = "workflowId")
    })
    Transition toEntity(TransitionAdminDTO dto);

    @Mappings({
            @Mapping(target = "statutOrigine", source = "statutOrigineCode"),
            @Mapping(target = "defaultStatutDestination", source = "defaultStatutDestinationCode"),
            @Mapping(target = "workflow", source = "workflowId")
    })
    void updateEntity(TransitionAdminDTO dto, @MappingTarget Transition entity);

    default Type mapType(String code) {
        return code == null || code.isBlank() ? null : new Type(code);
    }

    default Workflow mapWorkflow(Long id) {
        if (id == null) return null;
        Workflow w = new Workflow();
        w.setId(id);
        return w;
    }
}
