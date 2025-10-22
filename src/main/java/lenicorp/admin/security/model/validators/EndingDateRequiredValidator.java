package lenicorp.admin.security.model.validators;

import lenicorp.admin.security.model.dtos.UserProfileAssoDTO;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EndingDateRequiredValidator.EndingDateRequiredValidatorImpl.class})
@Documented
public @interface EndingDateRequiredValidator {
    String message() default "La date de fin est obligatoire lorsque le type d'assignation est intérim";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Component
    @RequiredArgsConstructor
    class EndingDateRequiredValidatorImpl implements ConstraintValidator<EndingDateRequiredValidator, UserProfileAssoDTO> {
        @Override
        public boolean isValid(UserProfileAssoDTO dto, ConstraintValidatorContext context) {
            if (dto == null) return true;
            
            // If userProfileAssTypeCode is not USR_PRFL_TYPE, no validation needed
            if (dto.getUserProfileAssTypeCode() == null || !dto.getUserProfileAssTypeCode().equals("INTERIM")) {
                return true;
            }
            
            // If userProfileAssTypeCode is USR_PRFL_TYPE, endingDate must not be null
            return dto.getEndingDate() != null;
        }
    }
}