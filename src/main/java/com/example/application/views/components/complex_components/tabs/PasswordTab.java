package com.example.application.views.components.complex_components.tabs;

import com.example.application.views.pages.SettingsView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
@Route(value = "settings/password", layout = SettingsView.class)
public class PasswordTab extends Div {

    private final H1 title = new H1("Change Password");
    private final PasswordField currentPassword = new PasswordField("Current Password");
    private final PasswordField newPassword = new PasswordField("New Password");
    private final PasswordField confirmPassword = new PasswordField("Confirm New Password");
    private final Button changePasswordButton = new Button("Change Password");

    public PasswordTab() {
        add(getContent());
    }

    public Div getContent() {
        Div div = new Div();
        div.add(title, currentPassword, newPassword, confirmPassword, changePasswordButton);
        return div;
    }

}
