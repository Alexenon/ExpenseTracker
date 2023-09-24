package com.example.application.views.pages;

import com.example.application.utils.StringUtils;
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.router.RouteConfiguration;

@Tag(Tag.MAIN)
public abstract class AbstractPage extends Main implements HasNotifications {

    /**
     * Changes page URL in the browser, without reloading the page.
     */
    public void updatePageUrl(String url) {
        String deepLinkingUrl = RouteConfiguration.forSessionScope().getUrl(getClass());
        UI.getCurrent().getPage().getHistory().replaceState(null, deepLinkingUrl + url);
    }

    /**
     * Changes the page title, without reloading the page.
     */
    public void updatePageTitle(String title) {
        title = StringUtils.replaceSpecialCharacters(title);
        title = StringUtils.replaceWithCapitalLetter(title);
        UI.getCurrent().getPage().setTitle(title);
    }

}
