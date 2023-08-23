package com.example.application.views.main.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;

@Tag("Div")
@JsModule("./themes/light_theme/components/javascript/login-script.js")
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
                            <input type="text" name="username" placeholder="Enter Username"/>
                            <label for="password">Password</label>
                            <input type="password" name="password" placeholder="Enter Password"/>
                            <button type="submit">Login</button>
                        </form>
                        """
        );

    }

}
