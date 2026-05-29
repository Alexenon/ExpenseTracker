package com.example.application.views.components.utils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.theme.lumo.LumoIcon;

public class CommonButtons {

	private CommonButtons() {
	}

	public static Button createAddButton(String text) {
		Button button = new Button(text);
		button.setIcon(LumoIcon.PLUS.create());
		button.setIconAfterText(true);
		return button;
	}

}
