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

        Div container = new Div();
        container.addClassName("container");

        Div panelsContainer = new Div();
        panelsContainer.addClassName("panels-container");
        panelsContainer.add(getPanelContent());

        container.add(loginComponent, panelsContainer);
        add(container);
    }

    private Div getPanelContent() {
        Div leftPanel = new Div();
        leftPanel.addClassNames("panel", "left-panel");

        Div content = new Div();
        content.addClassNames("content");

        H3 h3 = new H3("New here ?");
        Paragraph p = new Paragraph("Lorem ipsum, dolor sit amet consectetur adipisicing elit. Debitis, ex ratione. Aliquid!");
        Button btn = new Button("Sign up", e -> getUI().ifPresent(ui -> ui.navigate(RegistrationView.class)));
        btn.addClassNames("btn", "transparent");

        Image image = new Image("images/logo_black.png", "Login panel image");
        image.addClassName("image");

        content.add(h3, p, btn);
        leftPanel.add(content, image);

        return leftPanel;
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
