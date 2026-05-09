package com.example.application.views.components.custom;

import com.example.application.views.pages.HomeView;
import com.example.application.views.pages.crypto.calculator.AssetCalculatorView;
import com.example.application.views.pages.crypto.portfolio.PortfolioTrackerView;
import com.example.application.views.pages.expenses.DashboardView;
import com.example.application.views.pages.expenses.ExpensesView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.router.RouterLink;

import java.util.List;

@Tag(Tag.NAV)
public class NavigationBar extends Nav {

	private final Image logoImage = new Image("/images/logos/logo-white-background.png", "Logo image");
	private final RouterLink expensesLink = new RouterLink("Expenses", ExpensesView.class);
	private final RouterLink dashboardLink = new RouterLink("Dashboard", DashboardView.class);
	private final RouterLink portfolioLink = new RouterLink("Portfolio", PortfolioTrackerView.class);
	private final RouterLink calculatorLink = new RouterLink("Calculator", AssetCalculatorView.class);

	public NavigationBar() {
		addClassName("navbar");

		logoImage.addClassNames("logo");
		logoImage.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(HomeView.class)));

		Div innerMenu = new Div();
		innerMenu.addClassName("menu-inner");
		innerMenu.add(logoImage);
		innerMenu.add(getRoutes());
		innerMenu.getChildren().forEach(c -> c.addClassName("menu-item"));

		Div menu = new Div(innerMenu);
		menu.setClassName("menu");

		add(menu);
	}

	public List<Component> getRoutes() {
		return List.of(expensesLink, dashboardLink, portfolioLink, calculatorLink);
	}

	public Image getLogo() {
		return logoImage;
	}

}
