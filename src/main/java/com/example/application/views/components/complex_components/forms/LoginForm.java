package com.example.application.views.components.complex_components.forms;

import com.example.application.views.components.basic_components.Form;
import com.example.application.views.components.basic_components.NativeButton;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import lombok.Getter;

@Tag("Form")
public class LoginForm extends Form {

    private final H2 title;
    @Getter
    private final TextField username;
    @Getter
    private final PasswordField password;
    @Getter
    private final NativeButton submitBtn;

    public LoginForm() {
        this.setAction("login");
        this.setMethod("post");

        title = new H2("Login");
        username = new TextField(null, "Enter your username");
        password = new PasswordField(null, "Enter password");
        submitBtn = new NativeButton("Submit Now");

        title.addClassName("form-title");
        username.setId("username-field");
        password.setId("password-field");
        submitBtn.addClassName("submit-btn");

        add(title, username, password, submitBtn);
        setupAtributesForInputFields();
    }

    private void setupAtributesForInputFields() {
        String script = """
                const usernameField = document.getElementById('username-field');
                const usernameInput = usernameField.querySelector('input');
                usernameInput.setAttribute('name', 'username');
                                
                const passwordField = document.getElementById('password-field');
                const passwordInput = passwordField.querySelector('input');
                passwordInput.setAttribute('name', 'password');
                passwordInput.setAttribute('autocomplete', 'current-password');
                """;

        UI.getCurrent().getPage().executeJs(script);
    }

    public void setTitleVisible(boolean visible) {
        title.setVisible(visible);
    }

}
