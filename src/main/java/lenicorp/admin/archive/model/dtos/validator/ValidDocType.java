package lenicorp.admin.archive.model.dtos.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lenicorp.admin.types.controller.repositories.TypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ValidDocType.ValidArchiveTypeValidator.class})
@Documented
public @interface ValidDocType
{
    String message() default "Type d'archive invalide";
    Class<?> [] groups() default {};
    Class<? extends Payload> [] payload() default {};

    @Component
    class ValidArchiveTypeValidator implements ConstraintValidator<ValidDocType, String>
    {
        @Autowired
        TypeRepo typeRepo;

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context)
        {
            if(value == null) return true;
            return typeRepo.existsByCodeAndGroupCode(value, "DOC");
        }
    }
}
