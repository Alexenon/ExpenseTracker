package com.example.application.views.main.components;

import com.example.application.views.main.RegistrationView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;

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
        signInContainer.addClassName("signup-container");

        Component loginForm = getLoginForm();

        Div panelsContainer = new Div();
        panelsContainer.addClassName("panels-container");
        panelsContainer.add(getPanelContent());

        signInContainer.add(errorWrapper, loginForm);
        container.add(signInContainer, panelsContainer);

        add(container);
    }

    private Component getLoginForm() {
        return new Html("""      
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
                """);
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

    public void setError(boolean error) {
        if (error) {
            errorWrapper.add(errorMessage);
        } else {
            errorWrapper.remove(errorMessage);
        }
    }

}
