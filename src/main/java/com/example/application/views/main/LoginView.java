package com.example.application.views.main;

import com.example.application.views.main.components.LoginComponent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
public class LoginView extends Main implements BeforeEnterObserver {

    private final LoginComponent loginComponent;

    public LoginView() {
        addClassName("login-page");
        loginComponent = new LoginComponent();
        loginComponent.addClassName("signin-container");

        Div panelsContainer = new Div(getPanelContent());
        panelsContainer.addClassNames("panels-container", "position-left");

        add(loginComponent, panelsContainer);
    }

    private Div getPanelContent() {
        Div leftPanel = new Div();
        leftPanel.addClassNames("panel", "left-panel");

        Div content = new Div();
        content.addClassNames("content");

        H3 h3 = new H3("Welcome to IncomeMate!");
        Paragraph p = new Paragraph("Enter your personal details and start journey with us!");
        Button btn = new Button("Sign up", e -> getUI().ifPresent(ui -> ui.navigate(RegistrationView.class)));
        btn.addClassNames("btn", "transparent");

        Image image = new Image("images/log.svg", "Login page background image");
        image.addClassName("image");

        content.add(h3, p, btn);
        leftPanel.add(content, image);

        return leftPanel;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        boolean containsError = beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error");

        loginComponent.setError(containsError);
    }

}
