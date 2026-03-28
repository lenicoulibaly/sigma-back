package lenicorp.metier.payment.model.validators;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lenicorp.admin.types.controller.repositories.TypeRepo;
import lenicorp.admin.utilities.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ExistingModePaiement.Validator.class})
@Documented
public @interface ExistingModePaiement {
    String message() default "Le mode de paiement avec le code '{validatedValue}' n'existe pas";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default false;

    @Component
    class Validator implements ConstraintValidator<ExistingModePaiement, String> {
        @Autowired
        private TypeRepo typeRepo;

        private boolean allowNull;

        @Override
        public void initialize(ExistingModePaiement constraintAnnotation) {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(String code, ConstraintValidatorContext context) {
            if (StringUtils.isBlank(code)) return allowNull;
            return typeRepo.existsByCodeAndGroupCode(code.toUpperCase(), "MODE_PAIEMENT");
        }
    }
}
