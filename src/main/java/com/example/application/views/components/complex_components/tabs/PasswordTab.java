package com.example.application.views.components.complex_components.tabs;

import com.example.application.views.components.basic_components.Form;
import com.example.application.views.components.basic_components.LabeledInput;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.textfield.PasswordField;
import jakarta.annotation.security.PermitAll;

import java.util.Optional;

@PermitAll
public class PasswordTab extends Div {

    private final H2 title = new H2("Change Password");
//    private final PasswordField currentPassword = new PasswordField();
//    private final PasswordField newPassword = new PasswordField();
//    private final PasswordField confirmPassword = new PasswordField();

    private final LabeledInput<PasswordField> currentPassword;
    private final LabeledInput<PasswordField> newPassword;
    private final LabeledInput<PasswordField> confirmPassword;
    private final NativeButton submitBtn = new NativeButton("Change password");

    public PasswordTab() {
        addClassName("tab-container");

        currentPassword = new LabeledInput<>("Current password", new PasswordField());
        newPassword = new LabeledInput<>("New password", new PasswordField());
        confirmPassword = new LabeledInput<>("Confirm new password", new PasswordField());

        addStyleToElements();

        add(title, getChangePasswordForm());
    }

    private void addStyleToElements() {
        title.addClassName("tab-title");

        currentPassword.addClassName("settings-form-group");
        newPassword.addClassName("settings-form-group");
        confirmPassword.addClassName("settings-form-group");

        currentPassword.setInputId("current-password");
        newPassword.setInputId("new-password");
        confirmPassword.setInputId("confirm-password");

        String newPasswordFieldId = newPassword.getInputField().getId().orElseThrow();
        confirmPassword.setNativeInputAttribute("autocomplete", newPasswordFieldId);

        submitBtn.getElement().setAttribute("type", "submit");
    }

    private Form getChangePasswordForm() {
        Form form = new Form();
        form.setMethod("POST");
        form.setAction("/");
        form.addClassName("settings-password-form");
        form.add(
                currentPassword, newPassword, confirmPassword,
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

