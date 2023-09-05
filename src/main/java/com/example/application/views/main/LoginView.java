package com.example.application.views.main;

import com.example.application.views.main.components.LoginForm;
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

    private final LoginForm loginForm;
    private final Div errorWrapper;

    public LoginView() {
        addClassName("login-page");
        loginForm = new LoginForm();
        initLoginForm();

        errorWrapper = getErrorWrapper();

        Div loginContainer = new Div(errorWrapper, loginForm);
        loginContainer.addClassName("signin-container");

        Div panelsContainer = new Div();
        panelsContainer.addClassNames("panels-container", "position-left");
        panelsContainer.add(getPanelContent());

        add(loginContainer, panelsContainer);
    }

    private void initLoginForm() {
        loginForm.addClassName("sign-in-form");

        H2 title = new H2("Sign in");
        title.addClassName("title");
        loginForm.addComponentAsFirst(title);

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
        boolean containsError = beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error");

        showError(containsError);
    }

    private void showError(boolean error) {
        errorWrapper.setVisible(error);
    }

    private Div getErrorWrapper() {
        Div div = new Div();
        div.addClassName("error-wrapper");
        Paragraph errorText = new Paragraph("Wrong username or password");
        div.add(errorText);
        return div;
    }

}
