package com.example.application.views.components.complex_components.tabs;

import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLink;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RouteTabs extends Tabs implements BeforeEnterObserver {

    private final Map<RouterLink, Tab> routerLinkTabMap = new HashMap<>();

    public void add(RouterLink routerLink) {
        routerLink.setHighlightCondition(HighlightConditions.sameLocation());
        routerLink.setHighlightAction(
                (link, shouldHighlight) -> {
                    if (shouldHighlight) setSelectedTab(routerLinkTabMap.get(routerLink));
                }
        );

        setOrientation(Tabs.Orientation.VERTICAL);

        Tab tab = new Tab(routerLink);
        tab.getElement().addEventListener("click", event -> {
        });

        routerLinkTabMap.put(routerLink, tab);

        add(routerLinkTabMap.get(routerLink));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        Optional<RouterLink> firstKey = routerLinkTabMap.keySet().stream().findFirst();
        Tab firstTab = routerLinkTabMap.get(firstKey.orElseThrow());
        setSelectedTab(firstTab);
    }

}