package com.example.application.views.main.components.complex_components.forms;

import com.example.application.views.main.components.basic_components.NativeInput;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import lombok.Getter;

@Tag("form")
public class RegisterForm extends Div {

    @Getter
    private final NativeInput username;
    @Getter
    private final NativeInput email;
    @Getter
    private final NativeInput password;
    @Getter
    private final NativeInput confirmPassword;
    @Getter
    private final Button submitBtn;

    public RegisterForm() {
        username = new NativeInput("username", "text", "Enter your username");
        email = new NativeInput("email", "email", "Enter your email");
        password = new NativeInput("password", "password", "Enter password");
        confirmPassword = new NativeInput("confirm-password", "password", "Enter confirmation password");
        submitBtn = new Button("Submit Now");

        initForm();
    }

    private void initForm() {
        Div usernameField = getField(username, "username");
        Div emailField = getField(email, "password");
        Div passwordField = getField(password, "password");
        Div confirmPasswordField = getField(confirmPassword, "confirm-password");

        Div submitField = getInputField(submitBtn);
        submitField.addClassNames("btn");

        add(
                usernameField,
                emailField,
                passwordField,
                confirmPasswordField,
                submitField
        );
    }

    private Div getField(Component field, String name) {
        Div inputField = getInputField(field);
        Div div = new Div(inputField);
        div.addClassNames("field", name);
        return div;
    }

    private Div getInputField(Component field) {
        Div div = new Div(field);
        div.addClassName("input-field");
        return div;
    }

}
