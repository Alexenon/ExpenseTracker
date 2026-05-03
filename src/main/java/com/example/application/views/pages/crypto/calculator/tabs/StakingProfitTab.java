package com.example.application.views.pages.crypto.calculator.tabs;

import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.finance.FinancialConstants;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.formatters.number.AmountFormatter;
import com.example.application.utils.common.formatters.number.CurrencyFormatter;
import com.example.application.views.components.custom.fields.AmountField;
import com.example.application.views.components.custom.fields.AssetComboBox;
import com.example.application.views.components.custom.fields.MoneyField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.example.application.utils.investment.EarnCalculator.*;

public final class StakingProfitTab extends BaseCalculatorTab {

	private static final AmountFormatter amountFormatter = AmountFormatter.withDefaults();
	private static final CurrencyFormatter currencyFormatter = CurrencyFormatter.withDefaults();

	private final PortfolioDTO portfolio;

	private final AssetComboBox assetSymbolField;
	private final AmountField amountField = new AmountField("Amount of tokens");
	private final MoneyField worthField = new MoneyField("Current total worth");
	private final AmountField aprField = new AmountField("APR");

	@Autowired
	public StakingProfitTab(InstrumentsFacadeService instrumentsFacadeService) {
		super("Staking calculator", instrumentsFacadeService);
		this.assetSymbolField = new AssetComboBox(instrumentsFacadeService);
		this.portfolio = instrumentsFacadeService.getActivePortfolio();
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		buildForm();
	}

	private void buildForm() {
		initializeFieldsValues();
		initializeFieldsListeners();
	}

	private void initializeFieldsValues() {
		aprField.setValue("1");
		aprField.setSuffixComponent(new Span("%"));
	}

	private void initializeFieldsListeners() {
		assetSymbolField.addValueChangeListener(field -> {
			String symbol = assetSymbolField.getSymbol().orElse("");
			BigDecimal amountTokens = assetSymbolField.getAmountTokens(portfolio.getId()).orElse(BigDecimal.ZERO);
			BigDecimal marketPrice = assetSymbolField.getMarketPrice().orElse(BigDecimal.ZERO);

			amountField.setValue(amountTokens);
			amountField.setSuffixComponent(new Span(symbol));

			BigDecimal worth = amountTokens.multiply(marketPrice);
			worthField.setValue(worth);
		});

		amountField.setValueChangeMode(ValueChangeMode.EAGER);
		amountField.addKeyUpListener(e -> {
			BigDecimal amount = amountField.getAmount();
			BigDecimal marketPrice = assetSymbolField.getMarketPrice().orElse(BigDecimal.ZERO);

			worthField.setValue(amount.multiply(marketPrice));
		});

		worthField.setValueChangeMode(ValueChangeMode.EAGER);
		worthField.addKeyUpListener(e -> {
			BigDecimal worth = worthField.getMoneyAmount();
			BigDecimal marketPrice = assetSymbolField.getMarketPrice().orElse(BigDecimal.ZERO);
			BigDecimal amountOfTokens = worth.divide(marketPrice, FinancialConstants.AMOUNT_SCALE, RoundingMode.HALF_UP);
			amountField.setValue(amountOfTokens);
		});
	}

	@Override
	protected Div createInputFieldsContainer() {
		return new Div(assetSymbolField, amountField, worthField, aprField);
	}

	@Override
	protected Button createDisplayResultsBtn() {
		Button calculateBtn = new Button("Calculate");
		calculateBtn.addClassName("add-entity-btn");
		calculateBtn.addClickListener(e -> {
			BigDecimal apr = aprField.getAmount();
			BigDecimal worth = worthField.getMoneyAmount();
			BigDecimal amount = amountField.getAmount();

			String daily = stakedAmount(earnDaily(amount, apr), earnDaily(worth, apr));
			String weekly = stakedAmount(earnWeekly(amount, apr), earnWeekly(worth, apr));
			String monthly = stakedAmount(earnMonthly(amount, apr), earnMonthly(worth, apr));
			String yearly = stakedAmount(earnYearly(amount, apr), earnYearly(worth, apr));

			resultsContainer.removeAll();
			resultsContainer.add(
					createResultItem("Daily", daily),
					createResultItem("Weekly", weekly),
					createResultItem("Monthly", monthly),
					createResultItem("Yearly", yearly)
			);
		});

		return calculateBtn;
	}

	private String stakedAmount(BigDecimal amountTokens, BigDecimal worthEquivalent) {
		return "+ %s %s ≈ %s".formatted(
				amountFormatter.format(amountTokens),
				assetSymbolField.getSymbol(),
				currencyFormatter.format(worthEquivalent)
		);
	}

}
