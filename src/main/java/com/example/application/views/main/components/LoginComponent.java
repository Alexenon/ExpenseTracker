package com.example.application.views.main.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;

@Tag("Div")
public class LoginComponent extends Div {

    private final Div errorWrapper;
    private final Text errorMessage = new Text("Wrong username or password");

    public LoginComponent() {
        errorWrapper = new Div();
        errorWrapper.addClassName("error-wrapper");

        Div container = new Div();
        container.addClassName("container");

        Div signInContainer = new Div();
        signInContainer.addClassName("signin-signup");

        Component loginForm = getLoginForm();

        signInContainer.add(errorWrapper, loginForm);
        container.add(signInContainer);

        add(container);
    }

    private Component getLoginForm() {
        return new Html(
                """      
                        <form id="login-form" method="post" action="login" class="sign-in-form">
                            <h2 class="title">Sign in</h2>
                            <div class="field email-field">
                                <div class="input-field">
                                    <input type="email" placeholder="Enter your email" class="email">
                                </div>
                            </div>
                                
                            <div class="field create-password">
                                <div class="input-field">
                                    <input type="password" placeholder="Enter password" class="password">
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
                        """
        );

    }

    public void setError(boolean error) {
        if (error) {
            errorWrapper.add(errorMessage);
        } else {
            errorWrapper.remove(errorMessage);
        }
    }

}
