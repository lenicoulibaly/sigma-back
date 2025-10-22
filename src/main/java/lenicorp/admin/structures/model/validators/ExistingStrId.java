package lenicorp.admin.structures.model.validators;

import lenicorp.admin.structures.controller.repositories.StrRepo;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistingStrId.Validator.class)
@Documented
public @interface ExistingStrId
{
    String message() default "Structure introuvable avec l'identifiant '{validatedValue}'";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default false;

    @Component
    @RequiredArgsConstructor
    class Validator implements ConstraintValidator<ExistingStrId, Long>
    {
        private final StrRepo strRepo;
        private boolean allowNull = true;

        @Override
        public void initialize(ExistingStrId constraintAnnotation)
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(Long strId, ConstraintValidatorContext context)
        {
            if (strId == null) return allowNull;
            return strRepo.existsById(strId);
        }
    }
}