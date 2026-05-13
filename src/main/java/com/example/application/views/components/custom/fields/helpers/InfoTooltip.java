package com.example.application.views.components.custom.fields.helpers;

import com.example.application.views.components.custom.icons.MonoIcon;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.vaadin.flow.component.shared.Tooltip;

public class InfoTooltip {

	private final MonoIcon infoIcon;

	public InfoTooltip(String text) {
		infoIcon = PictogramIcon.INFORMATION_OUTLINE.create();
		infoIcon.addClassName("tooltip-info-icon");
		Tooltip tooltip = Tooltip.forComponent(infoIcon);
		tooltip.setPosition(Tooltip.TooltipPosition.TOP);
		tooltip.setText(text);
	}

	public MonoIcon getIcon() {
		return infoIcon;
	}

}
