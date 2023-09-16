package com.example.application.views.components.basic_components;

import com.example.application.views.components.utils.TextFieldValidationSupport;
import com.vaadin.flow.component.PropertyDescriptor;
import com.vaadin.flow.component.PropertyDescriptors;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.shared.HasAllowedCharPattern;
import com.vaadin.flow.data.binder.Validator;

import java.util.Optional;

@Tag(Tag.INPUT)
public class NativeInput extends Input implements HasAllowedCharPattern {

    private static final PropertyDescriptor<String, Optional<String>> nameDescriptor = PropertyDescriptors.optionalAttributeWithDefault("name", "");
    private TextFieldValidationSupport validationSupport;

    public NativeInput() {
    }

    public NativeInput(String type) {
        this.setType(type);
    }

    public NativeInput(String type, String placeholder) {
        this.setType(type);
        this.setPlaceholder(placeholder);
    }

    public NativeInput(String name, String type, String placeholder) {
        this.setType(type);
        this.setPlaceholder(placeholder);
        this.setName(name);
    }

    public void setName(String name) {
        this.set(nameDescriptor, name);
    }

    private TextFieldValidationSupport getValidationSupport() {
        if (this.validationSupport == null) {
            this.validationSupport = new TextFieldValidationSupport(this);
        }

        return this.validationSupport;
    }

    public void setMaxLength(int maxLength) {
        this.getElement().setProperty("maxlength", maxLength);
        this.getValidationSupport().setMaxLength(maxLength);
    }

    public int getMaxLength() {
        return (int)this.getElement().getProperty("maxlength", 0.0D);
    }

    public void setMinLength(int minLength) {
        this.getElement().setProperty("minlength", minLength);
        this.getValidationSupport().setMinLength(minLength);
    }

    public int getMinLength() {
        return (int)this.getElement().getProperty("minlength", 0.0D);
    }

    public void setRequired(boolean required) {
        this.getElement().setProperty("required", required);
        this.getValidationSupport().setRequired(required);
    }

    public void setPattern(String pattern) {
        this.getElement().setProperty("pattern", pattern == null ? "" : pattern);
        this.getValidationSupport().setPattern(pattern);
    }

    public String getPattern() {
        return this.getElement().getProperty("pattern");
    }

    public String getEmptyValue() {
        return "";
    }

    public void setValue(String value) {
        super.setValue(value);
    }

    public String getValue() {
        return super.getValue();
    }

    public void setRequiredIndicatorVisible(boolean requiredIndicatorVisible) {
        super.setRequiredIndicatorVisible(requiredIndicatorVisible);
        this.getValidationSupport().setRequired(requiredIndicatorVisible);
    }

    public Validator<String> getDefaultValidator() {
        return (value, context) -> this.getValidationSupport().checkValidity(value);
    }

}
