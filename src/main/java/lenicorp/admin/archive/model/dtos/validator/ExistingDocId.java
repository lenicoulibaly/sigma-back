package lenicorp.admin.archive.model.dtos.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lenicorp.admin.archive.controller.repositories.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ExistingDocId.ExistingDocIdValidator.class})
@Documented
public @interface ExistingDocId
{
    String message() default "Document inexistant";
    Class<?> [] groups() default {};
    Class<? extends Payload> [] payload() default {};

    @Component
    @RequiredArgsConstructor
    class ExistingDocIdValidator implements ConstraintValidator<ExistingDocId, Long>
    {
        private final DocumentRepository docRepo;

        @Override
        public boolean isValid(Long value, ConstraintValidatorContext context)
        {
            if(value == null) return true;
            return docRepo.findById(value) != null;
        }
    }
}
