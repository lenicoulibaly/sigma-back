package lenicorp.admin.security.model.validators;

import lenicorp.admin.security.controller.repositories.AuthAssoRepo;
import lenicorp.admin.security.model.dtos.UserProfileAssoDTO;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UniqueUserProfileAssociation.UniqueUserProfileAssociationValidatorForCreate.class})
@Documented
@Repeatable(UniqueUserProfileAssociation.List.class)
public @interface UniqueUserProfileAssociation
{
    String message() default "Ce profile est déjà attribué à cet utilisateur pour la même structure";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.FIELD}) @Retention(RetentionPolicy.RUNTIME) @Documented
    @interface List
    {
        UniqueUserProfileAssociation[] value();
    }

    @Component
    @RequiredArgsConstructor
    class UniqueUserProfileAssociationValidatorForCreate implements ConstraintValidator<UniqueUserProfileAssociation, UserProfileAssoDTO>
    {
        private final AuthAssoRepo authAssoRepo;

        @Override
        public boolean isValid(UserProfileAssoDTO dto, ConstraintValidatorContext context)
        {
            if (dto == null) return true;
            return !authAssoRepo.existsByUserIdAndProfileCodeAndStrId(dto.getUserId(), dto.getProfileCode(), dto.getStrId());
        }
    }
}