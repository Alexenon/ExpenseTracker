package com.example.application.views.pages.auth;

import com.example.application.views.components.LoginComponent;
import com.example.application.views.pages.AbstractPage;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login")
@CssImport("./themes/light_theme/styles/page-styles/auth-pages.css")
public class LoginView extends AbstractPage implements BeforeEnterObserver {

    private final LoginComponent loginComponent;

    public LoginView() {
        setId("login-page");
        loginComponent = new LoginComponent();
        loginComponent.addClassName("login-container");

        add(loginComponent, getPanelContainer());
    }

    private Div getPanelContainer() {
        H3 h3 = new H3("Welcome to IncomeMate!");
        Paragraph p = new Paragraph("Enter your personal details and start journey with us!");
        Button btn = new Button("Register", e -> getUI().ifPresent(ui -> ui.navigate(RegistrationView.class)));
        Image image = new Image("images/log.svg", "Login page background image");

        btn.addClassNames("btn", "transparent");
        image.addClassName("image");

        Div content = new Div(h3, p, btn);
        content.addClassNames("content");

        Div leftPanel = new Div(content, image);
        leftPanel.addClassNames("panel", "left-panel");

        Div panelsContainer = new Div(leftPanel);
        panelsContainer.addClassNames("panels-container", "position-left");

        return panelsContainer;
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
