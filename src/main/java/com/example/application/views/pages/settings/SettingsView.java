package com.example.application.views.pages.settings;

import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.DefaultPage;
import com.example.application.views.pages.settings.tabs.CategoriesTab;
import com.example.application.views.pages.settings.tabs.SettingsAbstractTab;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

@PermitAll
@PageTitle("Settings")
@Route(value = "settings", layout = MainLayout.class)
public class SettingsView extends DefaultPage {

	private InstrumentsFacadeService instrumentsFacadeService;

	private final Tab profile = new Tab(VaadinIcon.USER.create(), new Span("Profile"));
	private final Tab categoriesTab;
	private final Tab labels = new Tab(VaadinIcon.EYE.create(), new Span("Labels"));
	private final VerticalLayout content = new VerticalLayout();

	@Autowired
	public SettingsView(InstrumentsFacadeService instrumentsFacadeService) {
		this.instrumentsFacadeService = instrumentsFacadeService;
		this.categoriesTab = new CategoriesTab(instrumentsFacadeService);
		getStyle().set("margin-top", "150px");


		Tabs tabs = new Tabs(profile, categoriesTab, labels);
		tabs.setOrientation(Tabs.Orientation.VERTICAL);
		tabs.setHeight("240px");
		tabs.setWidth("240px");

		tabs.addSelectedChangeListener(event -> setContent(event.getSelectedTab()));

		add(tabs, content);
	}

	private void setContent(Tab tab) {
		content.removeAll();

		if (!(tab instanceof SettingsAbstractTab selectedTab))
			return;

		content.add(selectedTab.getContent());
	}


}
