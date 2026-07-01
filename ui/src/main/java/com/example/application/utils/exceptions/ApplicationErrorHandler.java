package com.example.application.utils.exceptions;

import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.server.DefaultErrorHandler;
import com.vaadin.flow.server.ErrorEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationErrorHandler extends DefaultErrorHandler implements HasNotifications {

    public void error(ErrorEvent errorEvent) {
        log.error("Something wrong happened", errorEvent.getThrowable());

        if (errorEvent.getThrowable() instanceof InternalUnexpectedException e) {
            showErrorNotification("There is an unexpected error, please share the steps");
        } else {
            showWarningNotification("DEV NOTE -> Exception caught, please check logs!");
        }

    }

}