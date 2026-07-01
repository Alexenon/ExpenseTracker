package com.example.application;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EntityValidator {

    private final Validator validator;

    public EntityValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> void validate(T object) {
        Map<String, String> errors = validateSafe(object);
        if (!errors.isEmpty())
            throw new InputValidationException(errors);
    }

    public <T> Map<String, String> validateSafe(T object) {
        return validator.validate(object)
                .stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));
    }

}