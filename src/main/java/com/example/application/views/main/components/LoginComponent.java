package com.example.application.views.main.components;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.html.Div;

@Tag("Div")
public class LoginComponent extends Div {

    private final Div errorWrapper;
    private final Text errorMessage = new Text("Wrong username or password");

    public LoginComponent() {
        this.addClassName("signup-container");

        HtmlContainer form = new HtmlContainer("form");

        Component loginForm = getLoginForm();
        loginForm.addClassName("sign-in-form");

        errorWrapper = new Div();
        errorWrapper.addClassName("error-wrapper");

        add(errorWrapper, loginForm, form);
    }

    private Component getLoginForm() {
        return new Html("""      
                <form id="login-form" method="post" action="login">
                    <h2 class="title">Sign in</h2>
                    <div class="field username-field">
                        <div class="input-field">
                            <input type="text" name="username" placeholder="Enter your username" class="username">
                        </div>
                    </div>
                    <div class="field password-field">
                        <div class="input-field">
                            <input type="password" name="password" placeholder="Enter password" class="password">
                            <i class='bx bx-hide show-hide'></i>
                        </div>
                    </div>
                    <div class="input-field button btn">
                        <input type="submit" value="Submit Now" />
                    </div>
                    <div class="forgot-password">
                        <a href="/forgot-password">
                            <p>Forgot password?</p>
                        </a>
                    </div>
                </form>
                """);
    }

    public void setError(boolean error) {
        if (error) {
            errorWrapper.add(errorMessage);
        } else {
            errorWrapper.remove(errorMessage);
        }
    }

}
