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

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UniqueTypeName.FieldValidator.class, UniqueTypeName.TypeValidator.class})
@Documented
public @interface UniqueTypeName 
{
    String message() default "Le nom de type '{validatedValue}' existe déjà";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    @Component
    class FieldValidator implements ConstraintValidator<UniqueTypeName, String> 
    {
        @Autowired
        private TypeRepo typeRepo;

        @Override
        public boolean isValid(String name, ConstraintValidatorContext context) 
        {
            if (StringUtils.isBlank(name)) return true;
            return !typeRepo.existsByName(name);
        }
    }

    @Component
    class TypeValidator implements ConstraintValidator<UniqueTypeName, TypeDTO> 
    {
        @Autowired
        private TypeRepo typeRepo;

        @Override
        public boolean isValid(TypeDTO dto, ConstraintValidatorContext context)
        {
            if (dto == null || StringUtils.isBlank(dto.getName())) return true;
            String name = dto.getName();
            String code = dto.getCode();
            boolean exists = StringUtils.isBlank(code) ? 
                typeRepo.existsByName(name) : 
                typeRepo.existsByNameAndNotCode(name, code);
            return !exists;
        }
    }
}
