package com.example.application.views.pages.exception_pages;

import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.springframework.http.HttpStatus;

@AnonymousAllowed
@PageTitle("Not Found")
public class NotFoundView extends ExceptionView<NotFoundException> {
    @Override
    protected HttpStatus httpStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    protected String errorTitle() {
        return "Page not found!";
    }

    @Override
    protected String imageSource() {
        return "./images/error-pages/404.png";
    }

    @Override
    protected String getErrorDescription(ErrorParameter<NotFoundException> parameter) {
        return "Couldn't find the page you were looking for";
    }
}

