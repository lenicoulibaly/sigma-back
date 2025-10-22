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
@Constraint(validatedBy = NotExpiredToken.Validator.class)
@Documented
public @interface NotExpiredToken
{
    String message() default "Token expiré";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default true;

    @Component
    @RequiredArgsConstructor
    class Validator implements ConstraintValidator<NotExpiredToken, String>
    {
        private final AuthTokenRepo authTokenRepo;
        private boolean allowNull;

        @Override
        public void initialize(NotExpiredToken constraintAnnotation)
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(String token, ConstraintValidatorContext context)
        {
            if (token == null) return allowNull;

            return authTokenRepo.tokenHasNotExpired(token);
        }
    }
}