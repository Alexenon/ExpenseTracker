package com.example.application.views.pages;

import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

public abstract class DefaultPage extends Main implements BeforeEnterObserver {

    /**
     * This method represents the initialization of page
     */
    protected abstract void initializePage();

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
    protected abstract void buildPage();

    /**
     * Method designed to initialize child components with types like:
     * <li>values
     * <li>styles
     * <li>listeners
     */
    protected abstract void initializeComponents();

    protected void rebuildPage() {
        this.removeAll();
        buildPage();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        initializePage();
        initializeComponents();
        buildPage();
    }

}
