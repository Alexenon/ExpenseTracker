package com.example.application.views.main.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.LoginForm;

public class LoginRichContent extends Div {

    public LoginRichContent() {
        addClassName("login-rich-content");

        LoginForm loginForm = new LoginForm();
        loginForm.getElement().getThemeList().add("dark");
        add(loginForm);
        loginForm.getElement().setAttribute("no-autofocus", "");
    }

}