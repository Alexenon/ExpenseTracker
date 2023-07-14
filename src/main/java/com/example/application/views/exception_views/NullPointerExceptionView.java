package com.example.application.views.exception_views;

import jakarta.servlet.http.HttpServletResponse;

class NullPointerExceptionView extends ExceptionView<NullPointerException> {
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