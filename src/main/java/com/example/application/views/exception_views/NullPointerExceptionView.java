package com.example.application.views.exception_views;

import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.PageTitle;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;

@PermitAll
@PageTitle("Not Found")
public class NullPointerExceptionView extends ExceptionView<NullPointerException> {
    @Override
    protected int httpStatus() {
        return HttpServletResponse.SC_NOT_FOUND;
    }

    @Override
    protected String errorTitle() {
        return "Something went wrong!";
    }

    @Override
    protected String imageSource() {
        return "./images/404-error.png";
    }

    @Override
    protected String getErrorMessage(ErrorParameter<NullPointerException> parameter) {
        return parameter.getException().getMessage();
    }
}