package com.example.application.views.pages.exception_pages;

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
        return "This page was not found!";
    }

    @Override
    protected String imageSource() {
        return "./images/404.webp";
    }

}