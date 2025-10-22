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
@Constraint(validatedBy = {UniqueTel.UniqueTelValidatorForCreate.class, UniqueTel.UniqueTelValidatorForUpdate.class})
@Documented
public @interface UniqueTel
{
    String message() default "Le numéro de téléphone '{validatedValue}' existe déjà";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default false;

    @Component @RequiredArgsConstructor
    class UniqueTelValidatorForCreate implements ConstraintValidator<UniqueTel, String>
    {
        private final UserRepo userRepo;
        private boolean allowNull;

        @Override
        public void initialize(UniqueTel constraintAnnotation)
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(String tel, ConstraintValidatorContext context)
        {
            if (StringUtils.isBlank(tel)) return allowNull;
            return !userRepo.existsByTel(tel.toUpperCase());
        }
    }

    @Component @RequiredArgsConstructor
    class UniqueTelValidatorForUpdate implements ConstraintValidator<UniqueTel, UserDTO>
    {
        private final UserRepo userRepo;
        private boolean allowNull;

        @Override
        public void initialize(UniqueTel constraintAnnotation)
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(UserDTO dto, ConstraintValidatorContext context)
        {
            if (dto == null) return true;
            String tel = dto.getTel();
            if (StringUtils.isBlank(tel)) return allowNull;
            return !userRepo.existsByTel(tel.toUpperCase(), dto.getUserId());
        }
    }
}
