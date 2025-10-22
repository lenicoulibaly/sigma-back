package lenicorp.admin.structures.model.validators;

import lenicorp.admin.structures.controller.repositories.StrRepo;
import lenicorp.admin.structures.model.dtos.ChangeAnchorDTO;
import lenicorp.admin.structures.model.dtos.CreateOrUpdateStrDTO;
import lenicorp.admin.utilities.StringUtils;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {
        UniqueSigleUnderSameParent.UniqueSigleUnderSameParentValidator.class,
        UniqueSigleUnderSameParent.UniqueSigleUnderSameParentValidatorOnChangeAnchor.class
})
@Documented
public @interface UniqueSigleUnderSameParent
{
    String message() default "Ce sigle existe déjà sous le même parent";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Component
    @RequiredArgsConstructor
    class UniqueSigleUnderSameParentValidator implements ConstraintValidator<UniqueSigleUnderSameParent, CreateOrUpdateStrDTO>
    {
        private final StrRepo strRepo;

        @Override
        public boolean isValid(CreateOrUpdateStrDTO dto, ConstraintValidatorContext context)
        {
            if (dto == null || StringUtils.isBlank(dto.getStrSigle()))
            {
                return true;
            }

            Long excludeStrId = (dto.getStrId() != null) ? dto.getStrId() : null;

            return !strRepo.sigleExistsUnderSameParent(
                    dto.getStrSigle(),
                    dto.getParentId(),
                    excludeStrId
            );
        }
    }

    @Component
    @RequiredArgsConstructor
    class UniqueSigleUnderSameParentValidatorOnChangeAnchor implements ConstraintValidator<UniqueSigleUnderSameParent, ChangeAnchorDTO>
    {
        private final StrRepo strRepo;

        @Override
        public boolean isValid(ChangeAnchorDTO dto, ConstraintValidatorContext context)
        {
            if (dto == null || StringUtils.isBlank(dto.getStrSigle()))
            {
                return true;
            }

            Long excludeStrId = (dto.getStrId() != null) ? dto.getStrId() : null;

            return !strRepo.sigleExistsUnderSameParent(
                    dto.getStrSigle(),
                    dto.getParentId(),
                    excludeStrId
            );
        }
    }
}