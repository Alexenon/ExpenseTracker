package com.example.application.views.pages.settings;

import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.DefaultPage;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
public class SettingsView extends DefaultPage {

	public SettingsView() {
		Tab profile = new Tab(VaadinIcon.USER.create(), new Span("Profile"));
		Tab categories = new Tab(VaadinIcon.COG.create(), new Span("Categories"));
		Tab labels = new Tab(VaadinIcon.EYE.create(), new Span("Labels"));

		Tabs tabs = new Tabs(profile, categories, labels);
		tabs.setOrientation(Tabs.Orientation.VERTICAL);
		tabs.setHeight("240px");
		tabs.setWidth("240px");

		add(tabs);
	}


}
