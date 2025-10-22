package lenicorp.admin.security.model.validators;

import lenicorp.admin.security.controller.repositories.UserRepo;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ExistingUserId.Validator.class)
@Documented
public @interface ExistingUserId
{
    String message() default "L'utilisateur avec ID '{validatedValue}' n'existe pas";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default true;

    @Component @RequiredArgsConstructor
    class Validator implements ConstraintValidator<ExistingUserId, Long>
    {
        private final UserRepo userRepo;
        private boolean allowNull;

        @Override
        public void initialize(ExistingUserId constraintAnnotation)
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(Long userId, ConstraintValidatorContext context)
        {
            if (userId == null) return allowNull;
            return userRepo.existsById(userId);
        }
    }
}
