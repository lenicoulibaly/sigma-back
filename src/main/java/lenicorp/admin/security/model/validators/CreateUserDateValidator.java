package lenicorp.admin.security.model.validators;

import lenicorp.admin.security.model.dtos.CreateUserDTO;
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
@Constraint(validatedBy = {CreateUserDateValidator.CreateUserDateValidatorImpl.class})
@Documented
public @interface CreateUserDateValidator {
    String message() default "Erreur de validation des dates";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Component
    @RequiredArgsConstructor
    class CreateUserDateValidatorImpl implements ConstraintValidator<CreateUserDateValidator, CreateUserDTO> {
        @Override
        public boolean isValid(CreateUserDTO dto, ConstraintValidatorContext context)
        {
            if (dto == null) return true;
            
            // If userProfileAssTypeCode is "USR_PRFL_TYPE", endingDate must not be null
            if (dto.getUserProfileAssTypeCode() != null && dto.getUserProfileAssTypeCode().equals("INTERIM") && dto.getEndingDate() == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("La date de fin est obligatoire lorsque le type d'assignation est Intérim")
                       .addConstraintViolation();
                return false;
            }
            
            // If endingDate is null, no further validation needed
            if (dto.getEndingDate() == null) return true;
            
            // If startingDate is not null, endingDate must be after startingDate
            if (dto.getStartingDate() != null && !dto.getEndingDate().isAfter(dto.getStartingDate())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("La date de fin doit être postérieure à la date de début")
                       .addConstraintViolation();
                return false;
            }
            
            // If startingDate is null, endingDate must be after today
            if (dto.getStartingDate() == null && !dto.getEndingDate().isAfter(LocalDate.now())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("La date de fin doit être postérieure à la date du jour si la date de début n'est pas spécifiée")
                       .addConstraintViolation();
                return false;
            }
            
            return true;
        }
    }
}