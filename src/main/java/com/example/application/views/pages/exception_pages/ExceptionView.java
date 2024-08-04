package com.example.application.views.pages.exception_pages;

import com.example.application.views.components.complex_components.ErrorContainer;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
public abstract class ExceptionView<T extends Exception> extends Main implements HasErrorParameter<T> {

    protected final ErrorContainer errorContainer;

    public ExceptionView() {
        addClassName("page-error");
        errorContainer = new ErrorContainer();
        add(errorContainer);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<T> parameter) {
        errorContainer.setErrorTitle(errorTitle());
        errorContainer.setErrorDescription(getErrorDescription(parameter));
        errorContainer.setImageSource(imageSource());
        
        return httpStatus();
    }

    protected String getErrorDescription(ErrorParameter<T> parameter) {
        return parameter.getException().toString();
    }

    protected abstract int httpStatus();

    protected abstract String errorTitle();

    protected abstract String imageSource();

}







