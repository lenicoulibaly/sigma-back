package lenicorp.admin.security.model.validators;

import lenicorp.admin.security.controller.repositories.UserRepo;
import lenicorp.admin.security.model.dtos.UserDTO;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ValidOldPassword.ValidOldPasswordValidator.class})
@Documented
public @interface ValidOldPassword
{
    String message() default "Ancien mot de passe ou username incorrect";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default true;

    @Component @RequiredArgsConstructor
    class ValidOldPasswordValidator implements ConstraintValidator<ValidOldPassword, UserDTO>
    {
        private final UserRepo userRepo;
        private final PasswordEncoder passwordEncoder;

        @Override
        public boolean isValid(UserDTO dto, ConstraintValidatorContext context)
        {
            if (dto == null) return true;
            String oldPassword = dto.getOldPassword();
            if (oldPassword == null) return true;
            String cryptedStoredPassword = userRepo.getPasswordByUsername(dto.getEmail());
            if( cryptedStoredPassword == null || cryptedStoredPassword.isBlank()) return true;
            return passwordEncoder.matches(oldPassword, cryptedStoredPassword);
        }
    }
}
