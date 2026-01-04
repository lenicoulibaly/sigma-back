package lenicorp.admin.utilities;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lenicorp.admin.workflowengine.controller.repositories.TransitionSideEffectRepository;
import lenicorp.admin.workflowengine.model.dtos.TransitionSideEffectDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UniqueSideEffectNameValidator implements ConstraintValidator<UniqueSideEffectName, TransitionSideEffectDTO> {

    private final TransitionSideEffectRepository repository;

    @Override
    public boolean isValid(TransitionSideEffectDTO dto, ConstraintValidatorContext context) {
        if (dto == null || dto.getName() == null || dto.getTransitionId() == null) {
            return true;
        }

        boolean exists;
        if (dto.getId() == null) {
            // Création
            exists = repository.existsByTransitionTransitionIdAndName(dto.getTransitionId(), dto.getName());
        } else {
            // Mise à jour
            exists = repository.existsByTransitionTransitionIdAndNameAndIdNot(dto.getTransitionId(), dto.getName(), dto.getId());
        }

        if (exists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("name")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
