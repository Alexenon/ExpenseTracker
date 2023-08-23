package com.example.application.views.main;

import com.example.application.views.main.components.LoginComponent;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends Main implements BeforeEnterObserver {

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        addClassName("login-page");

        /*
        * TODO: Add normal login form
        * */

        FormLayout layout;

        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle(null);
        i18n.setAdditionalInformation(null);

        loginForm.addClassNames("login-form", "remove-style");
        loginForm.setAction("login");
        loginForm.setI18n(i18n);
        loginForm.getChildren().forEach(c -> {
            c.addClassName("HERE");
        });

        NativeButton button = new NativeButton("Native button");

        LoginComponent loginComponent = new LoginComponent();

        add(loginComponent);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginForm.setError(true);
        }
    }
}
