package com.example.application.views.pages.exception_pages;

import com.example.application.utils.exceptions.auth.UnauthenticatedUserException;
import com.example.application.views.pages.LoginView;
import com.example.application.views.pages.RegistrationView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.PageTitle;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@PermitAll
@PageTitle("Not Found")
public class UnauthenticatedView extends ExceptionView<UnauthenticatedUserException> {

	public UnauthenticatedView() {
		Button login = new Button("Login", e -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
		Button register = new Button("Register", e -> getUI().ifPresent(ui -> ui.navigate(RegistrationView.class)));
		getContent().getButtonContainer().add(login, register);
	}

	@Override
    protected HttpStatus httpStatus() {
        return HttpStatus.UNAUTHORIZED;
    }

    @Override
    protected String errorTitle() {
        return "Oops! Unauthorize exception, please login in.";
    }

    @Override
    protected String imageSource() {
        return "./images/error-pages/404.png";
    }

    @Override
    protected String getErrorDescription(ErrorParameter<UnauthenticatedUserException> parameter) {
        return super.getErrorDescription(parameter);
    }

}