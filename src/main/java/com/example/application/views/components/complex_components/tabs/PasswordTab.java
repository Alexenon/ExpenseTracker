package com.example.application.views.components.complex_components.tabs;

import com.example.application.views.components.basic_components.Form;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.PasswordField;
import jakarta.annotation.security.PermitAll;

@PermitAll
public class PasswordTab extends Div {

    private final H2 title = new H2("Change Password");
    private final PasswordField currentPassword = new PasswordField();
    private final PasswordField newPassword = new PasswordField();
    private final PasswordField confirmPassword = new PasswordField();
    private final Button submitBtn = new Button();

    public PasswordTab() {
        addClassName("tab-container");

        addStyleToElements();

        add(title, getChangePasswordForm());
    }

    private void addStyleToElements() {
        title.addClassName("tab-title");

        currentPassword.setId("current-password");
        newPassword.setId("new-password");
        confirmPassword.setId("confirm-password");
    }

    private Form getChangePasswordForm() {
        Form form = new Form();
        form.addClassName("settings-password-form");
        form.add(
                getLabelWithInput("Current password", currentPassword),
                getLabelWithInput("New Password", newPassword),
                getLabelWithInput("Confirm password", confirmPassword),
                submitBtn
        );
        return form;
    }

    private Div getLabelWithInput(String labelName, Component inputField) {
        Label labelElement = new Label(labelName);
        labelElement.setFor(inputField);
        Div div = new Div(labelElement, inputField);
        div.addClassName("settings-form-group");
        return div;
    }

}

