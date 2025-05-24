package com.example.application.views.pages;

import com.example.application.views.components.custom.Footer;
import com.example.application.views.components.utils.HasNotifications;
import com.example.application.views.components.utils.HasScroll;
import com.vaadin.flow.component.html.Div;

public abstract class AbstractPage extends Div implements HasNotifications, HasScroll {

    protected Footer footer = new Footer();

}
