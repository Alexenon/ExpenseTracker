package com.example.application.views.pages.settings.tabs;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.AbstractIcon;
import com.vaadin.flow.component.tabs.Tab;

public abstract class SettingsAbstractTab extends Tab {

	public SettingsAbstractTab(String name, AbstractIcon<?> icon) {
		super(icon, new Span(name));
	}

}
