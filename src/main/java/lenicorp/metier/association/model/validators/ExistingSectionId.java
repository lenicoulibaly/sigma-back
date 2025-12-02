package lenicorp.metier.association.model.validators;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lenicorp.metier.association.controller.repositories.SectionRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ExistingSectionId.ExistingSectionIdValidator.class})
@Documented
public @interface ExistingSectionId
{
    String message() default "L'ID de la section est introuvable";
    Class<?> [] groups() default {};
    Class<? extends Payload> [] payload() default {};

    @Component
    @RequiredArgsConstructor
    class ExistingSectionIdValidator implements ConstraintValidator<ExistingSectionId, Long>
    {
        private final SectionRepo sectionRepo;

        @Override
        public boolean isValid(Long value, ConstraintValidatorContext context)
        {
            if(value == null) return true;
            return sectionRepo.existsById(value) ;
        }
    }
}