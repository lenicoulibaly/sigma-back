package lenicorp.admin.workflowengine.model.validators;

import lenicorp.admin.workflowengine.controller.repositories.WorkflowStatusGroupRepository;
import lenicorp.admin.utilities.StringUtils;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import lenicorp.admin.workflowengine.model.dtos.WorkflowStatusGroupDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueWorkflowStatusGroupCode.Validator.class)
@Documented
public @interface UniqueWorkflowStatusGroupCode 
{
    String message() default "Le code de groupe de statut '{validatedValue}' existe déjà";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    boolean allowNull() default false;

    @Component
    class Validator implements ConstraintValidator<UniqueWorkflowStatusGroupCode, Object> 
    {
        @Autowired
        private WorkflowStatusGroupRepository repository;
        private boolean allowNull;

        @Override
        public void initialize(UniqueWorkflowStatusGroupCode constraintAnnotation) 
        {
            this.allowNull = constraintAnnotation.allowNull();
        }

        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) 
        {
            String code = null;
            Long id = null;

            if (value instanceof String s) {
                code = s;
            } else if (value instanceof WorkflowStatusGroupDTO dto) {
                code = dto.getCode();
                id = dto.getId();
            }

            if (StringUtils.isBlank(code)) return allowNull;

            if (id != null) {
                return !repository.existsByCodeIgnoreCaseAndIdNot(code, id);
            }
            return !repository.existsByCode(code.toUpperCase());
        }
    }
}
