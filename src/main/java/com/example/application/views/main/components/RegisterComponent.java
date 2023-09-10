package com.example.application.views.main.components;

import com.example.application.views.main.components.complex_components.forms.RegisterForm;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;

public class RegisterComponent extends Div {

    private final RegisterForm registerForm;

    public RegisterComponent() {
        registerForm = new RegisterForm();
        registerForm.addClassName("sign-up-form");

        add(registerForm);
    }

    public TextField getUsernameField() {
        return registerForm.getUsername();
    }

    public TextField getEmailField() {
        return registerForm.getEmail();
    }

    public TextField getPasswordField() {
        return registerForm.getPassword();
    }

    public TextField getConfirmPasswordField() {
        return registerForm.getConfirmPassword();
    }

    public Button getSubmitButton() {
        return registerForm.getSubmitBtn();
    }

}
