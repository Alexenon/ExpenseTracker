package com.example.application.views.main.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.LoginForm;

public class LoginComponent extends Div {

    public LoginComponent() {
        addClassName("login-rich-content");

        com.vaadin.flow.component.login.LoginForm loginForm = new LoginForm();
        loginForm.getElement().getThemeList().add("dark");
        add(loginForm);
        loginForm.getElement().setAttribute("no-autofocus", "");
    }

}