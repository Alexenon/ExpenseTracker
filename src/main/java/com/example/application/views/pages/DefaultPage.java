package com.example.application.views.pages;

import com.example.application.views.components.utils.HasNotifications;
import com.example.application.views.components.utils.HasScroll;
import com.vaadin.flow.component.ScrollOptions;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

public abstract class DefaultPage extends Main implements BeforeEnterObserver, HasNotifications, HasScroll {

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

    protected void rebuildPage() {
        this.removeAll();
        buildPage();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        initializePage();
        rebuildPage();
    }

    public void scrollTopPage() {
        ScrollOptions scrollOptions = new ScrollOptions();
        scrollOptions.setBehavior(ScrollOptions.Behavior.AUTO);
        scrollOptions.setBlock(ScrollOptions.Alignment.START);
        scrollOptions.setInline(ScrollOptions.Alignment.START);
        this.getElement().scrollIntoView(scrollOptions);
    }

}
