package com.example.application.views.pages.exception_pages;

import com.example.application.utils.exceptions.auth.UnauthenticatedUserException;
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
		// TODO: [URGENT] Add 2 buttons -> to Login + Registration page
		getContent().getButtonContainer();
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