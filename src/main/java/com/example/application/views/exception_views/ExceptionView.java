package com.example.application.views.exception_views;

import com.example.application.views.main.components.ErrorContainer;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
public abstract class ExceptionView<T extends Exception> extends Main implements HasErrorParameter<T> {

    protected final ErrorContainer errorContainer;

    public ExceptionView() {
//        addClassName("page-error");
        errorContainer = new ErrorContainer();
        add(errorContainer);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<T> parameter) {
        errorContainer.setErrorTitle(errorTitle());
        errorContainer.setErrorDescription(getErrorMessage(parameter));
        errorContainer.setImageSource(imageSource());
        
        return httpStatus();
    }

    protected String getErrorMessage(ErrorParameter<T> parameter) {
        return parameter.hasCustomMessage()
                ? parameter.getCustomMessage()
                : parameter.getException().getMessage();
    }

    protected abstract int httpStatus();

    protected abstract String errorTitle();

    protected abstract String imageSource();

}







