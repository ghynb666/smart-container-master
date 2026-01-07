package cn.fuguang.utils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ValidationUtils {

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    public static <T> void validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);

        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation failed for: ");
            for (ConstraintViolation<T> violation : violations) {
                errorMessage.append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append("; ");
            }
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }
}
