package com.example.application.views.main.components;

import com.example.application.views.main.components.complex_components.forms.LoginForm;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;

@Tag("Div")
public class LoginComponent extends Div {

    private final LoginForm loginForm;
    private final ErrorWrapper errorWrapper;

    public LoginComponent() {
        loginForm = new LoginForm();
        loginForm.addClassName("sign-in-form");
        loginForm.addComponentAsFirst(getFormTitle());

        errorWrapper = new ErrorWrapper();
        errorWrapper.addClassName("error-wrapper");
        errorWrapper.setErrorMessage("Wrong username or password");

        add(errorWrapper, loginForm);
    }

    private H2 getFormTitle() {
        H2 title = new H2("Sign in");
        title.addClassName("title");
        return title;
    }

    public void setError(boolean error) {
        errorWrapper.setVisible(error);
    }

    static class ErrorWrapper extends Div {
        private final Paragraph errorMessage;

        public ErrorWrapper() {
            this.errorMessage = new Paragraph("Wrong username or password");
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage.setText(errorMessage);
        }
    }

}
