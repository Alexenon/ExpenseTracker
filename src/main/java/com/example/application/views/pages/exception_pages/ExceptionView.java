package com.example.application.views.pages.exception_pages;

import com.example.application.views.components.custom.ErrorContainer;
import com.example.application.views.pages.AbstractPage;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AnonymousAllowed
public abstract class ExceptionView<T extends Exception> extends AbstractPage implements HasErrorParameter<T> {

    private static final Logger log = LoggerFactory.getLogger(ExceptionView.class);

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
        log.error("Encountered client error: ");
        log.error(parameter.getException().toString());

        return httpStatus();
    }

    protected String getErrorDescription(ErrorParameter<T> parameter) {
        return parameter.getException().toString();
    }

    protected abstract int httpStatus();

    protected abstract String errorTitle();

    protected abstract String imageSource();

}







