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
@Constraint(validatedBy = ExistingEmail.Validator.class)
@Documented
public @interface ExistingEmail
{
    String message() default "Nom d'utilisateur ou mot de passe incorrect";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default true;

    @Component @RequiredArgsConstructor
    class Validator implements ConstraintValidator<ExistingEmail, String>
    {
        private final UserRepo userRepo;
        private boolean allowNull;

        @Override
        public void initialize(ExistingEmail constraintAnnotation)
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(String email, ConstraintValidatorContext context)
        {
            if (email == null) return allowNull;
            return userRepo.existsByEmail(email);
        }
    }
}
