package lenicorp.admin.security.model.validators;

import lenicorp.admin.security.controller.repositories.AuthTokenRepo;
import lenicorp.admin.security.model.dtos.UserDTO;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidToken.Validator.class)
@Documented
public @interface ValidToken
{
    String message() default "Token invalide pour l'utilisateur";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default true;

    @Component @RequiredArgsConstructor
    class Validator implements ConstraintValidator<ValidToken, UserDTO>
    {
        private final AuthTokenRepo authTokenRepo;
        private boolean allowNull;

        @Override
        public void initialize(ValidToken constraintAnnotation)
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(UserDTO dto, ConstraintValidatorContext context)
        {
            if (dto == null) return true;
            Long userId = dto.getUserId();
            String token = dto.getAuthToken();
            if (userId == null) return true;
            if (token == null || token.isBlank()) return allowNull;
            return authTokenRepo.existsByTokenAndUserId(token, userId);
        }
    }
}
