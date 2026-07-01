package com.example.application.views.components.custom.forms;

import com.example.application.views.components.core.Form;
import com.example.application.views.components.core.buttons.NativeButton;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

@Tag("Form")
public class LoginForm extends Form {

    private final H2 title = new H2("Login");
    private final TextField username = new TextField(null, "Enter your username");
    private final NativeButton submitBtn = new NativeButton("Sign In");
    private final PasswordField password = new PasswordField(null, "Enter password");

    public LoginForm() {
        this.setAction("login");
        this.setMethod("post");

        title.addClassName("form-title");
        username.setId("username-field");
        password.setId("password-field");
        submitBtn.addClassName("submit-btn");

        add(title, username, password, submitBtn);
        password.addAttachListener(e -> setupAttributesForInputFields());
    }

    private void setupAttributesForInputFields() {
        String script = """
                const usernameField = document.getElementById('username-field');
                const usernameInput = usernameField.querySelector('input');
                usernameInput.setAttribute('name', 'username');
                usernameInput.setAttribute('autocomplete', 'username');
                
                const passwordField = document.getElementById('password-field');
                const passwordInput = passwordField.querySelector('input');
                passwordInput.setAttribute('name', 'password');
                passwordInput.setAttribute('autocomplete', 'current-password');
                """;

        getUI().ifPresent(ui -> ui.getPage().executeJs(script));
    }

    public void setTitleVisible(boolean visible) {
        title.setVisible(visible);
    }

    public TextField getUsername() {
        return username;
    }

    public PasswordField getPassword() {
        return password;
    }

    public NativeButton getSubmitBtn() {
        return submitBtn;
    }
}
