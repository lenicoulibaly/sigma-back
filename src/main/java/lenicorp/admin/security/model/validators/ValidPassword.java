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
@Constraint(validatedBy = {ValidPassword.ValidPasswordValidator.class})
@Documented
public @interface ValidPassword
{
    String message() default "Mot de passe ou username incorrect";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default true;

    @Component @RequiredArgsConstructor
    class ValidPasswordValidator implements ConstraintValidator<ValidPassword, UserDTO>
    {
        private final UserRepo userRepo;
        private final PasswordEncoder passwordEncoder;

        @Override
        public boolean isValid(UserDTO dto, ConstraintValidatorContext context)
        {
            if (dto == null) return true;
            String password = dto.getPassword();
            if (password == null) return true;
            String cryptedStoredPassword = userRepo.getPasswordByUsername(dto.getEmail());
            if( cryptedStoredPassword == null || cryptedStoredPassword.isBlank()) return true;
            return passwordEncoder.matches(password, cryptedStoredPassword);
        }
    }
}
