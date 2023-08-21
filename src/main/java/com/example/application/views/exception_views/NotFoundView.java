package com.example.application.views.exception_views;

import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.servlet.http.HttpServletResponse;

@AnonymousAllowed
@PageTitle("Not Found")
public class NotFoundView extends ExceptionView<NotFoundException> {
    @Override
    protected int httpStatus() {
        return HttpServletResponse.SC_NOT_FOUND;
    }

    @Override
    protected String errorTitle() {
        return "Page not found!";
    }

    @Override
    protected String imageSource() {
        return "./images/404.webp";
    }

    @Override
    protected String getErrorMessage(ErrorParameter<NotFoundException> parameter) {
        return "Couldn't find the page you were looking for";
    }
}

