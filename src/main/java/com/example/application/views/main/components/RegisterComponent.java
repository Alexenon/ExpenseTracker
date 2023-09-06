package com.example.application.views.main.components;

import com.example.application.views.main.components.basic_components.NativeInput;
import com.example.application.views.main.components.complex_components.forms.RegisterForm;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

public class RegisterComponent extends Div {

    private final RegisterForm registerForm;

    public RegisterComponent() {
        this.addClassName("sign-up-form");

        registerForm = new RegisterForm();

        add(registerForm);
    }

    public NativeInput getUsernameField() {
        return registerForm.getUsername();
    }

    public NativeInput getEmailField() {
        return registerForm.getEmail();
    }

    public NativeInput getPasswordField() {
        return registerForm.getPassword();
    }

    public NativeInput getConfirmPasswordField() {
        return registerForm.getConfirmPassword();
    }

    public NativeInput getSubmitButton() {
        return registerForm.getSubmitBtn();
    }


}
