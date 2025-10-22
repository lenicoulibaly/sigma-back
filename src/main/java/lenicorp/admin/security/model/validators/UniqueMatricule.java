package lenicorp.admin.security.model.validators;

import lenicorp.admin.security.controller.repositories.UserRepo;
import lenicorp.admin.security.model.dtos.UserDTO;
import lenicorp.admin.security.model.dtos.UserProfileAssoDTO;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UniqueMatricule.UniqueMatriculeValidatorOnCreate.class, UniqueMatricule.UniqueMatriculeValidatorOnUpdate.class})
@Documented
public @interface UniqueMatricule
{
    String message() default "Matricule déjà utilisé";
    Class<?> [] groups() default {};
    Class<? extends Payload> [] payload() default {};


    @Component @RequiredArgsConstructor
    class UniqueMatriculeValidatorOnUpdate implements ConstraintValidator<UniqueMatricule, UserDTO>
    {
        private final UserRepo userRepo;

        @Override
        public boolean isValid(UserDTO dto, ConstraintValidatorContext context)
        {
            if(dto == null) return true;
            if(dto.getMatricule() == null || dto.getUserId() == null) return true;
            return !userRepo.existsByMatricule(dto.getMatricule(), dto.getUserId()) ;
        }
    }

    @Component @RequiredArgsConstructor
    class UniqueMatriculeValidatorOnUpdateUserAndProfile implements ConstraintValidator<UniqueMatricule, UserProfileAssoDTO>
    {
        private final UserRepo userRepo;

        @Override
        public boolean isValid(UserProfileAssoDTO dto, ConstraintValidatorContext context)
        {
            return !userRepo.existsByMatricule(dto.getMatricule(), dto.getUserId()) ;
        }
    }

    @Component @RequiredArgsConstructor
    class UniqueMatriculeValidatorOnCreate implements ConstraintValidator<UniqueMatricule, String>
    {
        private final UserRepo userRepo;

        @Override
        public boolean isValid(String matricule, ConstraintValidatorContext context)
        {
            return !userRepo.existsByMatricule(matricule) ;
        }
    }
}
