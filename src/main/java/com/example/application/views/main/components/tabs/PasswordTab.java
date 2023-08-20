package com.example.application.views.main.components.tabs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.textfield.PasswordField;

public class PasswordTab extends Div {

    private final H1 title = new H1("Change Password");
    private final PasswordField currentPassword = new PasswordField("Current Password");
    private final PasswordField newPassword = new PasswordField("New Password");
    private final PasswordField confirmPassword = new PasswordField("Confirm New Password");
    private final Button changePasswordButton = new Button("Change Password");

    public PasswordTab() {
        add(
                title,
                currentPassword,
                newPassword,
                confirmPassword,
                changePasswordButton
        );
    }

}
