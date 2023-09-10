package com.example.application.views.main.components.complex_components.forms;

import com.example.application.views.main.components.basic_components.Form;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

@Tag("form")
public class RegisterForm extends Form {

    @Getter
    private final TextField username;
    @Getter
    private final TextField password;
    @Getter
    private final TextField confirmPassword;
    @Getter
    private final TextField email;
    @Getter
    private final Button submitBtn;

    public RegisterForm() {
        username = new TextField(null, "Enter your username");
        password = new TextField(null, "Enter password");
        confirmPassword = new TextField(null, "Enter confirm password");
        email = new TextField(null, "Enter your email");
        submitBtn = new Button("Submit Now");

        add(username, password, confirmPassword, email, submitBtn);
    }

}
