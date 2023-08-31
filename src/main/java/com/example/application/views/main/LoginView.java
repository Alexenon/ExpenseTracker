package com.example.application.views.main;

import com.example.application.views.main.components.LoginComponent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
@JsModule("./themes/light_theme/components/javascript/login-script.js")
public class LoginView extends Main implements BeforeEnterObserver {

    private final LoginComponent loginComponent;

    public LoginView() {
        addClassName("login-page");

        loginComponent = new LoginComponent();

        add(loginComponent);

        UI.getCurrent().getPage().executeJs("addLoginListener()");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            loginComponent.setError(true);
        }
    }
}
