package com.example.application.views.components.portfolio.dialogs;

import com.example.application.portfolio.Portfolio;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.dialog.Dialog;

public class PortfolioDeletedEvent extends ComponentEvent<Dialog> {
	private final Portfolio portfolio;

	public PortfolioDeletedEvent(Dialog source, Portfolio portfolio) {
		super(source, false);
		this.portfolio = portfolio;
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}
}