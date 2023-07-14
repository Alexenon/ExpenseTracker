package com.example.application.views.exception_views;

import com.vaadin.flow.router.NotFoundException;
import jakarta.servlet.http.HttpServletResponse;

class NotFoundView extends ExceptionView<NotFoundException> {
    @Override
    protected int httpStatus() {
        return HttpServletResponse.SC_NOT_FOUND;
    }

    @Override
    protected String errorTitle() {
        return "This page was not found!";
    }

    @Override
    protected String imageSource() {
        return "./images/404.webp";
    }
}

