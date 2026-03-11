package com.example.application.views.components.portfolio.dialogs;

import com.example.application.entities.crypto.Portfolio;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.dialog.Dialog;

public class PortfolioCreatedOrUpdatedEvent extends ComponentEvent<Dialog> {
	private final Portfolio portfolio;

	public PortfolioCreatedOrUpdatedEvent(Dialog source, Portfolio portfolio) {
		super(source, false);
		this.portfolio = portfolio;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}
}