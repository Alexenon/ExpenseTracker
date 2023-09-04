package com.example.application.views.main.components;

import com.example.application.views.main.components.basic_components.Form;
import com.example.application.views.main.components.basic_components.NativeInput;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import lombok.Getter;

public class LoginForm extends Form {

    @Getter
    private final NativeInput username;
    @Getter
    private final NativeInput password;
    @Getter
    private final NativeInput submit;

    public LoginForm() {
        this.setId("login-form");
        this.setAction("login");
        this.setMethod("post");

        username = new NativeInput("username", "text", "Enter your username");
        password = new NativeInput("password", "password", "Enter password");
        submit = new NativeInput("submit");

        initForm();
    }

    private void initForm() {
        username.setId("username");
        password.setId("password");
        submit.setId("submit");
        submit.setValue("Submit Now");

        Div usernameField = getField(username, "username");
        Div passwordField = getField(password, "password");
        Div submitField = getField(submit, "submit");

        add(usernameField, passwordField, submitField);
    }

    private Div getField(Component field, String name) {
        Div div = new Div();
        div.addClassNames("field", name);
        Div inputField = new Div();
        inputField.addClassName("input-field");
        inputField.add(field);
        div.add(inputField);
        return div;
    }

}
