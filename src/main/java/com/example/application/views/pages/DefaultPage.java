package com.example.application.views.pages;

import com.vaadin.flow.component.ScrollOptions;

/*
* implements BeforeEnterObserver
* */
public abstract class DefaultPage extends AbstractPage {

    public void scrollTopPage() {
        ScrollOptions scrollOptions = new ScrollOptions();
        scrollOptions.setBehavior(ScrollOptions.Behavior.AUTO);
        scrollOptions.setBlock(ScrollOptions.Alignment.START);
        scrollOptions.setInline(ScrollOptions.Alignment.START);
        this.getElement().scrollIntoView(scrollOptions);
    }

}
