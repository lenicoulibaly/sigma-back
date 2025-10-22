package lenicorp.admin.types.model.validators;

import lenicorp.admin.types.controller.repositories.TypeRepo;
import lenicorp.admin.utilities.StringUtils;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueTypeCode.Validator.class)
@Documented
public @interface UniqueTypeCode 
{
    String message() default "Le code de type '{validatedValue}' existe déjà";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default false;

    @Component
    class Validator implements ConstraintValidator<UniqueTypeCode, String> 
    {
        @Autowired
        private TypeRepo typeRepo;
        private boolean allowNull;

        @Override
        public void initialize(UniqueTypeCode constraintAnnotation) 
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(String code, ConstraintValidatorContext context) 
        {
            if (StringUtils.isBlank(code)) return allowNull;
            return !typeRepo.existsByCode(code.toUpperCase());
        }
    }
}
