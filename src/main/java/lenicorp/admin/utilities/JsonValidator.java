package lenicorp.admin.utilities;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class JsonValidator implements ConstraintValidator<ValidJson, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // Consider null or empty as valid, use @NotBlank for that
        }
        try {
            mapper.readTree(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
