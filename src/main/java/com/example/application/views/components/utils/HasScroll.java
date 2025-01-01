package com.example.application.views.components.utils;

import com.vaadin.flow.component.UI;

public interface HasScroll {

    default void scrollTo(int width, int height) {
        String script = String.format("window.scrollTo(%d, %d)", width, height);
        UI.getCurrent().getPage().executeJs(script);
    }

    default void scrollBy(int width, int height) {
        String script = String.format("window.scrollBy(%d, %d)", width, height);
        UI.getCurrent().getPage().executeJs(script);
    }

}
