package com.example.application.views.main.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;

@Tag("Div")
public class LoginComponent extends Div {

    public LoginComponent() {
        Div errorWrapper = new Div();
        errorWrapper.addClassName("error-wrapper");

        add(errorWrapper, getComponent());
    }

    private Component getComponent() {
        return new Html(
                """
                        <form id="login-form" method="post" action="login">
                            <span class="error-message" id="error-message"></span>
                            <label for="username">Username</label>
                            <input id="username" type="text" name="username" placeholder="Enter Username" autocomplete="username" required/>
                            <label for="password">Password</label>
                            <input id="password" type="password" name="password" placeholder="Enter Password" autocomplete="current-password" required/>
                            <button type="submit">Login</button>
                        </form>
                        """
        );

    }

}
