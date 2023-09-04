package com.example.application.views.main.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

public class RegisterComponent extends Div {

    @Getter
    private final TextField username = new TextField("Username");
    @Getter
    private final PasswordField password = new PasswordField("Password");
    @Getter
    private final PasswordField confirmPassword = new PasswordField("Confirm password");
    @Getter
    private final EmailField email = new EmailField("Email");

    public RegisterComponent() {
        this.addClassName("sign-up-form");

        Div fieldUsername = getField("username", username);
        Div fieldPassword = getField("password", password);
        Div fieldConfirmPassword = getField("confirm-password", confirmPassword);
        Div fieldEmail = getField("email", email);

        add(fieldUsername, fieldPassword, fieldConfirmPassword, fieldEmail);
    }

    private Div getField(String fieldName, Component field) {
        Div div = new Div();
        div.addClassNames("field", fieldName);
        Div inputField = new Div();
        inputField.addClassName("input-field");
        inputField.add(field);
        div.add(inputField);
        return div;
    }


}
