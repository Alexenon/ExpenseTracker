package com.example.application.views.main.components.complex_components.forms;

import com.example.application.views.main.components.basic_components.Form;
import com.example.application.views.main.components.basic_components.NativeInput;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import lombok.Getter;

@Tag("Form")
public class LoginForm extends Form {

    @Getter
    private final NativeInput username;
    @Getter
    private final NativeInput password;
    @Getter
    private final NativeInput submitBtn;

    public LoginForm() {
        this.setId("login-form");
        this.setAction("login");
        this.setMethod("post");

        username = new NativeInput("username", "text", "Enter your username");
        password = new NativeInput("password", "password", "Enter password");
        submitBtn = new NativeInput("submit");

        initForm();
    }

    private void initForm() {
        submitBtn.setValue("Submit Now");

        Div usernameField = getField(username, "username");
        Div passwordField = getField(password, "password");
        Div submitField = getInputField(submitBtn);
        submitField.addClassNames("btn");

        add(usernameField, passwordField, submitField);
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
