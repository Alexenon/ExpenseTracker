package com.example.application.views.main.components.utils;

import com.vaadin.flow.component.HasValue;
import com.vaadin.flow.component.shared.ValidationUtil;
import com.vaadin.flow.data.binder.ValidationResult;

import java.io.Serializable;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public final class TextFieldValidationSupport implements Serializable {

    private final HasValue<?, String> field;
    private boolean required;
    private Integer minLength;
    private Integer maxLength;
    private Pattern pattern;

    public TextFieldValidationSupport(HasValue<?, String> field) {
        this.field = field;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern != null && !pattern.isEmpty() ? Pattern.compile(pattern) : null;
    }

    boolean isInvalid(String value) {
        ValidationResult requiredValidation = ValidationUtil.checkRequired(this.required, value, this.field.getEmptyValue());
        return requiredValidation.isError() || this.checkValidity(value).isError();
    }

    public ValidationResult checkValidity(String value) {
        boolean isMaxLengthExceeded = value != null && this.maxLength != null && value.length() > this.maxLength;
        if (isMaxLengthExceeded) {
            return ValidationResult.error("");
        } else {
            boolean isMinLengthNotReached = value != null && !value.isEmpty() && this.minLength != null && value.length() < this.minLength;
            if (isMinLengthNotReached) {
                return ValidationResult.error("");
            } else {
                boolean valueViolatePattern = value != null && !value.isEmpty() && this.pattern != null && !this.pattern.matcher(value).matches();
                return valueViolatePattern ? ValidationResult.error("") : ValidationResult.ok();
            }
        }
    }
}
