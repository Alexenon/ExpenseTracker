package com.example.application.views.components.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;

// FIXME: These doesn't work at all
public interface HasScroll {

    default void scrollTo(int width, int height) {
        UI.getCurrent().getPage().executeJs(
                "setTimeout(() => { window.scrollTo($0, $1); }, 0);",
                width, height
        );
    }

    default void scrollBy(int width, int height) {
        UI.getCurrent().getPage().executeJs(
                "setTimeout(() => { window.scrollBy($0, $1); }, 0);",
                width, height
        );
    }

    default void scrollBy(Component component, int width, int height) {
        component.getElement().executeJs("""
                setTimeout(function() {
                    this.scrollTo({left: $1, top: $2, behavior: "smooth"});
                }, 0);
                """, height, width);
    }

}
