package lenicorp.admin.security.model.validators;

import lenicorp.admin.security.controller.repositories.AuthorityRepo;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UniqueAuthCode.UniqueAuthCodeValidator.class})
@Documented
public @interface UniqueAuthCode
{
    String message() default "Code déjà utilisé";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default true;

    @Component @RequiredArgsConstructor
    class UniqueAuthCodeValidator implements ConstraintValidator<UniqueAuthCode, String>
    {
        private final AuthorityRepo authorityRepo;
        private boolean allowNull;

        @Override
        public void initialize(UniqueAuthCode constraintAnnotation)
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(String authCode, ConstraintValidatorContext context)
        {
            if (authCode == null) return allowNull;
            return !authorityRepo.existsByCode(authCode);
        }
    }
}