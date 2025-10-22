package lenicorp.admin.security.model.validators;

import lenicorp.admin.security.model.dtos.UserProfileAssoDTO;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;
import java.time.LocalDate;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DateConsistencyValidator.DateConsistencyValidatorImpl.class})
@Documented
public @interface DateConsistencyValidator {
    String message() default "La date de fin doit être postérieure à la date de début ou à la date du jour si la date de début est nulle";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Component
    @RequiredArgsConstructor
    class DateConsistencyValidatorImpl implements ConstraintValidator<DateConsistencyValidator, UserProfileAssoDTO> {
        @Override
        public boolean isValid(UserProfileAssoDTO dto, ConstraintValidatorContext context) {
            if (dto == null) return true;
            
            // If endingDate is null, no validation needed
            if (dto.getEndingDate() == null) return true;
            
            // If startingDate is not null, endingDate must be after startingDate
            if (dto.getStartingDate() != null) {
                return dto.getEndingDate().isAfter(dto.getStartingDate());
            }
            
            // If startingDate is null, endingDate must be after today
            return dto.getEndingDate().isAfter(LocalDate.now());
        }
    }
}