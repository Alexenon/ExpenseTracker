package com.example.application.views.pages.exception_pages;

import com.example.application.views.components.custom.ErrorContainer;
import com.example.application.views.pages.AbstractPage;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

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
        T exception = parameter.getException();
        errorContainer.setImageSource(imageSource());
        errorContainer.setErrorTitle(errorTitle());
        errorContainer.setErrorDescription(getErrorDescription(parameter));
        log.error("Encountered client error: {}", ExceptionUtils.getStackTrace(exception));
        return httpStatus().value();
    }

    protected String getErrorDescription(ErrorParameter<T> parameter) {
        return parameter.getException().toString();
    }

    protected abstract HttpStatus httpStatus();

    protected abstract String errorTitle();

    protected abstract String imageSource();

}







