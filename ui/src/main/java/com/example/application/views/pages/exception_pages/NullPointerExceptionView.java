package com.example.application.views.pages.exception_pages;

import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.PageTitle;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@PermitAll
@PageTitle("Not Found")
public class NullPointerExceptionView extends ExceptionView<NullPointerException> {
    @Override
    protected HttpStatus httpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Override
    protected String errorTitle() {
        return "Oops! Something went wrong.";
    }

    @Override
    protected String imageSource() {
        return "./images/error-pages/404.png";
    }

    @Override
    protected String getErrorDescription(ErrorParameter<NullPointerException> parameter) {
        return super.getErrorDescription(parameter);
    }
}