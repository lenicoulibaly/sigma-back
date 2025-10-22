package lenicorp.admin.security.model.validators;

import lenicorp.admin.security.model.dtos.UserDTO;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SamePasswordAndRepassword.Validator.class)
@Documented
public @interface SamePasswordAndRepassword
{
    String message() default "Les mots de passe '{validatedValue.password}' et {validatedValue.rePassword} ne correspondent pas";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default true;

    @Component
    class Validator implements ConstraintValidator<SamePasswordAndRepassword, UserDTO>
    {
        private boolean allowNull;

        @Override
        public void initialize(SamePasswordAndRepassword constraintAnnotation)
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(UserDTO dto, ConstraintValidatorContext context)
        {
            if (dto == null) return true;
            String password = dto.getPassword();
            String rePassword = dto.getRePassword();
            if( password == null || rePassword == null) return allowNull;
            return password.equals(rePassword);
        }
    }
}
