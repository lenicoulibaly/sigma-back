package lenicorp.admin.security.model.validators;

import lenicorp.admin.security.controller.repositories.AuthorityRepo;
import lenicorp.admin.security.model.dtos.AuthorityDTO;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UniqueAuthName.UniqueAuthNameValidatorForCreate.class, UniqueAuthName.UniqueAuthNameValidatorForUpdate.class})
@Documented
@Repeatable(UniqueAuthName.List.class)
public @interface UniqueAuthName
{
    String message() default "Nom déjà utilisé";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean allowNull() default true;

    @Target({ElementType.FIELD}) @Retention(RetentionPolicy.RUNTIME) @Documented
    @interface List
    {
        UniqueAuthName[] value();
    }

    @Component
    @RequiredArgsConstructor
    class UniqueAuthNameValidatorForCreate implements ConstraintValidator<UniqueAuthName, String>
    {
        private final AuthorityRepo authorityRepo;
        private boolean allowNull;

        @Override
        public void initialize(UniqueAuthName constraintAnnotation)
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(String authName, ConstraintValidatorContext context)
        {
            if (authName == null) return allowNull;
            return !authorityRepo.existsByName(authName);
        }
    }

    @Component
    @RequiredArgsConstructor
    class UniqueAuthNameValidatorForUpdate implements ConstraintValidator<UniqueAuthName, AuthorityDTO>
    {
        private final AuthorityRepo authorityRepo;
        private boolean allowNull;

        @Override
        public void initialize(UniqueAuthName constraintAnnotation)
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(AuthorityDTO dto, ConstraintValidatorContext context)
        {
            if (dto == null) return true;
            String authName = dto.getName();
            String authCode = dto.getCode();
            if (authName == null || authCode == null) return true;
            return !authorityRepo.existsByName(authName, authCode);
        }
    }
}
