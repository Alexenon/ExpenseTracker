package com.example.application.views.main;

import com.example.application.views.main.layouts.MainLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AnonymousAllowed
@PageTitle("Login")
@Route(value = "login", layout = MainLayout.class)
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private static final Logger logger = LoggerFactory.getLogger(LoginView.class);

    private static final String GITHUB_OAUTH_URL = "/oauth2/code/github";
    private static final String GOOGLE_OAUTH_URL = "/oauth2/code/google";

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {
        addClassNames("page-content", "login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        Anchor githubLoginLink = new Anchor(GITHUB_OAUTH_URL, "Login with Github");
        Anchor googleLoginLink = new Anchor(GOOGLE_OAUTH_URL, "Login with Google");
        githubLoginLink.getElement().setAttribute("router-ignore", true);
        googleLoginLink.getElement().setAttribute("router-ignore", true);

        loginForm.setAction("login");

        add(
                new H1("Welcome to Login Form"),
                loginForm,
                githubLoginLink,
                googleLoginLink
        );
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

    @SuppressWarnings("unused")
    private void setupLoginForm() {
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.setHeader(new LoginI18n.Header());
        i18n.getHeader().setTitle("Header Title");
        i18n.getHeader().setDescription("Header Description");
        i18n.getForm().setTitle("Title");
        i18n.setAdditionalInformation("Additional Information");

        loginForm.setAction("login");
        loginForm.setClassName("login-form");
        loginForm.addLoginListener(event -> {
            if (loginForm.isError()) {
                logger.info("User provided invalid credentials for login");
            } else {
                logger.info("User successfully logged in");
            }
        });
        loginForm.setI18n(i18n);
    }

}
