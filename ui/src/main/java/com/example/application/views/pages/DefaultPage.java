package com.example.application.views.pages;

import com.example.application.utils.exceptions.ApplicationErrorHandler;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ScrollOptions;
import com.vaadin.flow.component.UI;

/*
 * implements BeforeEnterObserver
 * */
public abstract class DefaultPage extends AbstractPage {

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI.getCurrent().getSession().setErrorHandler(new ApplicationErrorHandler());
    }

    public void scrollTopOfThePage() {
        ScrollOptions scrollOptions = new ScrollOptions();
        scrollOptions.setBehavior(ScrollOptions.Behavior.AUTO);
        scrollOptions.setBlock(ScrollOptions.Alignment.START);
        scrollOptions.setInline(ScrollOptions.Alignment.START);
        this.getElement().scrollIntoView(scrollOptions);
    }

}
