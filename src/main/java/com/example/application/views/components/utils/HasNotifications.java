package com.example.application.views.components.utils;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

public interface HasNotifications {

    int DEFAULT_DURATION_MILLIS = 5000;
    Notification.Position DEFAULT_POSITION = Notification.Position.TOP_CENTER;

    default void showSuccessfulNotification(String text) {
        showSuccessfulNotification(text, DEFAULT_DURATION_MILLIS);
    }

    default void showSuccessfulNotification(String text, int durationMillis) {
        Notification notification = Notification.show(text);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(DEFAULT_POSITION);
        notification.setDuration(DEFAULT_DURATION_MILLIS);
        notification.open();
    }

    default void showErrorNotification(String text) {
        showErrorNotification(text, DEFAULT_DURATION_MILLIS);
    }

    default void showErrorNotification(String text, int durationMillis) {
        Notification notification = Notification.show(text);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(DEFAULT_POSITION);
        notification.setDuration(durationMillis);
        notification.open();
    }

    default void showWarningNotification(String text) {
        showWarningNotification(text, DEFAULT_DURATION_MILLIS);
    }

    default void showWarningNotification(String text, int durationMillis) {
        Notification notification = Notification.show(text);
        notification.addThemeVariants(NotificationVariant.LUMO_WARNING);
        notification.setPosition(DEFAULT_POSITION);
        notification.setDuration(durationMillis);
        notification.open();
    }



}
