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
        UniqueStrNameUnderSameParent.UniqueStrNameUnderSameParentValidator.class,
        UniqueStrNameUnderSameParent.UniqueStrNameUnderSameParentForChangeAnchorValidator.class
})
@Documented
public @interface UniqueStrNameUnderSameParent
{
    String message() default "Ce nom ('{validatedValue.strName}') existe déjà sous le même parent";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Component
    @RequiredArgsConstructor
    class UniqueStrNameUnderSameParentValidator implements ConstraintValidator<UniqueStrNameUnderSameParent, CreateOrUpdateStrDTO>
    {
        private final StrRepo strRepo;

        @Override
        public boolean isValid(CreateOrUpdateStrDTO dto, ConstraintValidatorContext context)
        {
            if (dto == null || StringUtils.isBlank(dto.getStrName()))
            {
                return true;
            }

            Long excludeStrId = (dto.getStrId() != null) ? dto.getStrId() : null;

            return !strRepo.strNameExistsUnderSameParent(
                    dto.getStrName(),
                    dto.getParentId(),
                    excludeStrId
            );
        }
    }

    @Component
    @RequiredArgsConstructor
    class UniqueStrNameUnderSameParentForChangeAnchorValidator implements ConstraintValidator<UniqueStrNameUnderSameParent, ChangeAnchorDTO>
    {
        private final StrRepo strRepo;

        @Override
        public boolean isValid(ChangeAnchorDTO dto, ConstraintValidatorContext context)
        {
            if (dto == null || StringUtils.isBlank(dto.getStrName()))
            {
                return true;
            }
            return !strRepo.strNameExistsUnderSameParent(
                    dto.getStrName(),
                    dto.getParentId(),
                    dto.getStrId()
            );
        }
    }
}