package lenicorp.admin.security.model.validators;

import lenicorp.admin.security.controller.repositories.UserRepo;
import lenicorp.admin.security.model.dtos.UserDTO;
import lenicorp.admin.utilities.StringUtils;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UniqueEmail.UniqueEmailValidatorForCreate.class, UniqueEmail.UniqueEmailValidatorForUpdate.class})
@Documented
public @interface UniqueEmail
{
    String message() default "L'adresse mail '{validatedValue}' existe déjà";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default false;

    @Component @RequiredArgsConstructor
    class UniqueEmailValidatorForCreate implements ConstraintValidator<UniqueEmail, String>
    {
        private final UserRepo userRepo;
        private boolean allowNull;

        @Override
        public void initialize(UniqueEmail constraintAnnotation)
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(String email, ConstraintValidatorContext context)
        {
            if (StringUtils.isBlank(email)) return allowNull;
            return !userRepo.existsByEmail(email.toUpperCase());
        }
    }

    @Component @RequiredArgsConstructor
    class UniqueEmailValidatorForUpdate implements ConstraintValidator<UniqueEmail, UserDTO>
    {
        private final UserRepo userRepo;
        private boolean allowNull;

        @Override
        public void initialize(UniqueEmail constraintAnnotation)
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(UserDTO dto, ConstraintValidatorContext context)
        {
            if (dto == null) return true;
            String email = dto.getEmail();
            if (StringUtils.isBlank(email)) return allowNull;
            return !userRepo.existsByEmail(email.toUpperCase(), dto.getUserId());
        }
    }
}
