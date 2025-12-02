package lenicorp.admin.security.model.validators;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lenicorp.admin.types.controller.repositories.TypeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEmploiCode.Validator.class)
@Documented
public @interface ValidEmploiCode {
    String message() default "Emploi inconnu";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default true;

    @Component
    @RequiredArgsConstructor
    class Validator implements ConstraintValidator<ValidEmploiCode, String> {
        private final TypeRepo typeRepo;
        private boolean allowNull;

        @Override
        public void initialize(ValidEmploiCode constraintAnnotation) {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(String emploiCode, ConstraintValidatorContext context) {
            if (emploiCode == null) return allowNull;
            return typeRepo.existsByCodeAndGroupCode(emploiCode, "EMPLOI");
        }
    }
}
