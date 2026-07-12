package com.example.application.views.pages.settings;

import com.example.application.InstrumentsFacadeService;
import com.example.application.views.components.core.Container;
import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.DefaultPage;
import com.example.application.views.pages.settings.tabs.*;
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

	private final Tab profileTab;
	private final Tab passwordTab;
	private final Tab categoriesTab;
	private final Tab tagsTab;
	private final VerticalLayout content = new VerticalLayout();

	@Autowired
	public SettingsView(InstrumentsFacadeService instrumentsFacadeService) {
		this.profileTab = new ProfileTab(instrumentsFacadeService);
		this.passwordTab = new PasswordTab(instrumentsFacadeService);
		this.categoriesTab = new CategoriesTab(instrumentsFacadeService);
		this.tagsTab = new TagsTab(instrumentsFacadeService);
		initialize();
	}

	private void initialize() {
		getStyle().set("margin-top", "150px");

		Tabs tabs = new Tabs(profileTab, passwordTab, categoriesTab, tagsTab);
		tabs.setOrientation(Tabs.Orientation.VERTICAL);
		tabs.setHeight("240px");
		tabs.setWidth("240px");
		tabs.addSelectedChangeListener(event -> setContent(event.getSelectedTab()));

		Container pageContent = Container.builder("settings-page-container")
				.addComponent(tabs)
				.addComponent(content)
				.build();

		add(pageContent);
		setContent(profileTab);
	}

	private void setContent(Tab tab) {
		content.removeAll();

		if (!(tab instanceof AbstractSettingsTab selectedTab))
			return;

		content.add(selectedTab.getContent());
	}


}
