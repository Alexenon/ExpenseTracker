package com.example.application.views.components.utils;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public interface HasNotifications {

    int DURATION_MILLIS = 5000;
    Notification.Position DEFAULT_POSITION = Notification.Position.TOP_CENTER;

    default void showSuccessfulNotification(String text) {
        Notification notification = Notification.show(text);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(DEFAULT_POSITION);
        notification.setDuration(DURATION_MILLIS);
        notification.open();
    }

    default void showErrorNotification(String text) {
        Notification notification = Notification.show(text);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(DEFAULT_POSITION);
        notification.setDuration(DURATION_MILLIS);
        notification.open();
    }

}
