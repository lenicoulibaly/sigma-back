package lenicorp.admin.utilities;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueSideEffectNameValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueSideEffectName {
    String message() default "Un effet de bord avec ce nom existe déjà pour cette transition";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
