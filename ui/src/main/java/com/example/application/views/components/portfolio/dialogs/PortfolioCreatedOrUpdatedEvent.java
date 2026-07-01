package com.example.application.views.components.portfolio.dialogs;

import com.example.application.data.dtos.PortfolioDTO;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.dialog.Dialog;

public class PortfolioCreatedOrUpdatedEvent extends ComponentEvent<Dialog> {
	private final PortfolioDTO portfolio;

	public PortfolioCreatedOrUpdatedEvent(Dialog source, PortfolioDTO portfolio) {
		super(source, false);
		this.portfolio = portfolio;
	}

	public PortfolioDTO getPortfolio() {
		return portfolio;
	}
}