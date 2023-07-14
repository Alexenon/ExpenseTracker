package com.example.application.views.exception_views;

import com.example.application.views.main.components.ErrorContainer;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;

@Tag(Tag.DIV)
public abstract class ExceptionView<T extends Exception> extends Div implements HasErrorParameter<T> {

    protected final ErrorContainer errorContainer;

    public ExceptionView() {
        addClassNames("page-error");
        errorContainer = new ErrorContainer();
        add(errorContainer);
    }

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<T> parameter) {
        String message = parameter.hasCustomMessage()
                ? parameter.getCustomMessage()
                : parameter.getException().getMessage();
        errorContainer.setErrorMessage(message);
        errorContainer.setErrorTitle(errorTitle());
        errorContainer.setImageSource(imageSource());
        
        return httpStatus();
    }

    protected abstract int httpStatus();

    protected abstract String errorTitle();

    protected abstract String imageSource();

}







