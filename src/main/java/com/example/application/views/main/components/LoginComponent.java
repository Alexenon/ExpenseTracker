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

        add(errorWrapper, getComponent());
    }

    private Component getComponent() {
        return new Html(
                """
                        <form id="login-form" method="post" action="login">
                            <label for="username">Username</label>
                            <input id="username" type="text" name="username" placeholder="Enter Username" autocomplete="username" required/>
                            <label for="password">Password</label>
                            <input id="password" type="password" name="password" placeholder="Enter Password" autocomplete="current-password" required/>
                            <button id="login-btn" type="submit">Login</button>
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
