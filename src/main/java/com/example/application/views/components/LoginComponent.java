package com.example.application.views.components;

import com.example.application.views.components.complex_components.forms.LoginForm;
import com.vaadin.flow.component.HtmlComponent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;

@Tag("Div")
public class LoginComponent extends Div {

    private final LoginForm loginForm;
    private final ErrorWrapper errorWrapper;

    public LoginComponent() {
        loginForm = new LoginForm();
        loginForm.setId("login-form");

        errorWrapper = new ErrorWrapper();
        errorWrapper.addClassName("error-wrapper");

        add(errorWrapper, loginForm);
    }

    public void setError(boolean error) {
        errorWrapper.setVisible(error);
    }

    public void addComponentToForm(HtmlComponent component) {
        loginForm.add(component);
    }

    public void addComponentAtIndexToForm(HtmlComponent component, int index) {
        loginForm.addComponentAtIndex(index, component);
    }

     static class ErrorWrapper extends Div {
        private final Paragraph errorMessage;

        public ErrorWrapper() {
            this.errorMessage = new Paragraph("Wrong username or password");
            add(errorMessage);
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage.setText(errorMessage);
        }
    }

}
