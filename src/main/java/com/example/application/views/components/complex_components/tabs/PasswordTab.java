package com.example.application.views.components.complex_components.tabs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.textfield.PasswordField;
import jakarta.annotation.security.PermitAll;

@PermitAll
public class PasswordTab extends Div {

    private final H3 title = new H3("Change Password");
    private final PasswordField currentPassword = new PasswordField("Current Password");
    private final PasswordField newPassword = new PasswordField("New Password");
    private final PasswordField confirmPassword = new PasswordField("Confirm New Password");
    private final Button changePasswordButton = new Button("Change Password");

    public PasswordTab() {
        addClassName("tab-container");

        addStyleToElements();

        add(title, getTabContent());
    }

    private void addStyleToElements() {
        title.addClassName("tab-title");

        currentPassword.addClassName("settings-form-group");
        newPassword.addClassName("settings-form-group");
        confirmPassword.addClassName("settings-form-group");
    }

    private Div getTabContent() {
        Div div = new Div(currentPassword, newPassword, confirmPassword, changePasswordButton);
        div.addClassName("settings-password-form");
        return div;
    }

}

