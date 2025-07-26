package com.example.application.views.pages.others;

import com.vaadin.flow.component.HasComponents;

public interface RebuildablePage extends HasComponents {

    /**
     * This method represents the initialization of page
     */
    default void initializePage() {
    }

    /**
     * This method represents how the page is build, and which elements should be attached to the page
     * <p> Example:
     * <pre>
     *      add(
     *          new H3("Title"),
     *          new Paragraph("Body")
     *      );
     * </pre>
     * This method can be reused to rebuilding page, to update some fields
     */
    void buildPage();

    default void rebuildPage() {
        this.removeAll();
        this.buildPage();
    }

}
