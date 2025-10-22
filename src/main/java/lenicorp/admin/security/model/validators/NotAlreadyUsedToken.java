package lenicorp.admin.security.model.validators;

import lenicorp.admin.security.controller.repositories.AuthTokenRepo;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotAlreadyUsedToken.Validator.class)
@Documented
public @interface NotAlreadyUsedToken
{
    String message() default "Token déjà utilisé";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default true;

    @Component @RequiredArgsConstructor
    class Validator implements ConstraintValidator<NotAlreadyUsedToken, String>
    {
        private final AuthTokenRepo authTokenRepo;
        private boolean allowNull;

        @Override
        public void initialize(NotAlreadyUsedToken constraintAnnotation)
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(String token, ConstraintValidatorContext context)
        {
            if (token == null) return allowNull;

            return !authTokenRepo.tokenIsAlreadyUsed(token);
        }
    }
}
