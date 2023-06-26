package com.example.application.model;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.server.DefaultErrorHandler;
import com.vaadin.flow.server.ErrorEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomErrorHandler extends DefaultErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorHandler.class);

    public void error(ErrorEvent errorEvent) {
        logger.error("Something wrong happened", errorEvent.getThrowable());
        if(UI.getCurrent() != null) {
            UI.getCurrent().access(() -> Notification.show("An internal error has occurred. Check logs"));
        }
    }

}