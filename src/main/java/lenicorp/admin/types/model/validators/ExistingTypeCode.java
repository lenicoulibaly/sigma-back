package lenicorp.admin.types.model.validators;

import lenicorp.admin.types.controller.repositories.TypeRepo;
import lenicorp.admin.types.model.dtos.TypeDTO;
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
@Constraint(validatedBy = {ExistingTypeCode.Validator.class, ExistingTypeCode.ExistingTypeCodeValidator.class})
@Documented
public @interface ExistingTypeCode 
{
    String message() default "Le type avec le code '{validatedValue}' n'existe pas";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default false;
    String typeGroupCode() default "";

    @Component
    class Validator implements ConstraintValidator<ExistingTypeCode, String> 
    {
        @Autowired
        private TypeRepo typeRepo;

        private boolean allowNull;

        @Override
        public void initialize(ExistingTypeCode constraintAnnotation) 
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(String code, ConstraintValidatorContext context)
        {
            if (StringUtils.isBlank(code)) return allowNull;
            System.out.println("================code============== : " + code);
            return typeRepo.existsByCode(code.toUpperCase());
        }
    }

    @Component
    class ExistingTypeCodeValidator implements ConstraintValidator<ExistingTypeCode, TypeDTO>
    {
        @Autowired
        private TypeRepo typeRepo;

        private boolean allowNull;

        @Override
        public void initialize(ExistingTypeCode constraintAnnotation)
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(TypeDTO dto, ConstraintValidatorContext context)
        {
            String code = dto.getCode();
            String groupCode = dto.getGroupCode();
            if (StringUtils.isBlank(code)) return allowNull;
            if(groupCode.equals("")) return typeRepo.existsByCode(code.toUpperCase());
            return typeRepo.existsByCodeAndGroupCode(code.toUpperCase(), groupCode);
        }
    }
}
