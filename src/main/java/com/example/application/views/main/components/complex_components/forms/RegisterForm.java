package com.example.application.views.main.components.complex_components.forms;

import com.example.application.views.main.components.basic_components.Form;
import com.example.application.views.main.components.basic_components.NativeButton;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

@Tag("form")
public class RegisterForm extends Form {

    @Getter
    private final TextField username;
    @Getter
    private final TextField email;
    @Getter
    private final PasswordField password;
    @Getter
    private final PasswordField confirmPassword;
    @Getter
    private final NativeButton submitBtn;

    public RegisterForm() {
        username = new TextField(null, "Enter your username");
        email = new TextField(null, "Enter your email");
        password = new PasswordField(null, "Enter password");
        confirmPassword = new PasswordField(null, "Enter confirm password");
        submitBtn = new NativeButton("Submit Now");

        add(username, email, password, confirmPassword, submitBtn);
    }

}
