package com.example.application.views.pages.crypto.calculator.tabs;

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.entities.crypto.Transaction;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.utils.investment.ProfitCalculator;
import com.example.application.utils.investment.ProfitUtils;
import com.example.application.views.components.core.ComponentBuilder;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.display.NumericValueParagraph;
import com.example.application.views.components.custom.fields.AssetComboBox;
import com.example.application.views.components.custom.fields.stats.ProfitStatsDisplay;
import com.example.application.views.components.custom.forms.layouts.TransactionalLayout;
import com.example.application.views.components.custom.icons.MonoIcon;
import com.example.application.views.components.custom.icons.PictogramIcon;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.theme.lumo.LumoIcon;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/*
    TODO: Work on styling table
        - https://colorlib.com/wp/css3-table-templates/
        - Fix table is not displayed

	TODO: Add dropdown-details with stats component
	    - Posibility to open and close some details (as there are a lot of them)

	TODO: Add buySellRatio with details:
			- 30 : 70
			- 4 buys ($340) : 9 sold ($1120)
			===> The TradingVolume + BuySellRatio can be merged into one stat

	TODO: Add Trading Volume with details:
			-  BUY "3496 ARB = $220", avg buy ...
			-  SELL "3496 ARB = $220", avg sell ...
			-  TOTAL VOLUME: "3496 ARB = $220"
			(maybe without decimal points for trading $ amount)
* */

/*
    | Type  | Symbol | Price | Amount tokens / currency | Total |
    | Buy   | SOL    | $110  | 0.23 SOL ~ $120          | $200  |
    | Sell  | SOL    | $130  | 0.23 SOL ~ $120          | $200  |
    | Buy   | SOL    | $130  | 0.23 SOL ~ $120          | $200  |
    | Sell  | SOL    | $130  | 0.23 SOL ~ $120          | $200  |
* */
@Slf4j
public class ProfitEmulatorTab extends BaseCalculatorTab {

	private final Portfolio portfolio;
	private final InstrumentsFacadeService instrumentsFacadeService;
	private final PortfolioPerformanceTracker portfolioPerformanceTracker;

	private final AssetComboBox assetSymbolField;
	private final List<TransactionalLayout> transactionalLayouts = new ArrayList<>();
	private final Button addNewLayoutBtn = new Button("Add Transaction", LumoIcon.PLUS.create());
	private final Container layoutsContainer = new Container("layout-container");
	private final Container metadataDetailsContainer = new Container("profit-meta-data");

	public ProfitEmulatorTab(InstrumentsFacadeService instrumentsFacadeService,
							 PortfolioPerformanceTracker portfolioPerformanceTracker)
	{
		super("Buy & Sell Emulator", instrumentsFacadeService);
		this.instrumentsFacadeService = instrumentsFacadeService;
		this.portfolioPerformanceTracker = portfolioPerformanceTracker;
		this.assetSymbolField = new AssetComboBox(instrumentsFacadeService);
		this.portfolio = instrumentsFacadeService.getActivePortfolio();
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		buildTab();
	}

	private void buildTab() {
		assetSymbolField.addValueChangeListener(field -> {
			if (field.getHasValue().isEmpty())
				return;

			Asset selectedAsset = field.getValue();
			transactionalLayouts.forEach(layout -> layout.setValue(selectedAsset));
			updateVisibilityForMetaData(selectedAsset);
		});

		assetSymbolField.getElement().getStyle()
				.set("width", "350px")
				.set("align-self", "center");
	}

	@Override
	protected Div createInputFieldsContainer() {
		addNewLayoutBtn.setIconAfterText(false);
		addNewLayoutBtn.addClickListener(e -> createNewLayout());
		addNewLayoutBtn.addClassName("add-entity-btn");

		TransactionalLayout defaultLayout = new TransactionalLayout(portfolio, instrumentsFacadeService, portfolioPerformanceTracker);
		defaultLayout.addClassName("buy-sell-layout");
		transactionalLayouts.add(defaultLayout);
		layoutsContainer.add(defaultLayout);
		add(metadataDetailsContainer);

		return new Div(assetSymbolField, layoutsContainer, addNewLayoutBtn);
	}

	@Override
	protected Button createDisplayResultsBtn() {
		Button button = new Button("Calculate", e -> {
			String symbol = assetSymbolField.getSymbol();
			List<Transaction> transactions = getListOfTransactions();

			double price = assetSymbolField.getSelectedAsset().getMarketPrice();
			double avgBuy = ProfitCalculator.averageBuyPrice(transactions);
			double avgSell = ProfitCalculator.averageSellPrice(transactions);
			double amountOfRemainingTokens = ProfitCalculator.getAmountOfRemainingTokens(transactions);
			double realizedProfit = ProfitCalculator.realizedProfit(transactions);
			double unrealizedProfit = amountOfRemainingTokens * assetSymbolField.getMarketPrice();
			double totalProfit = realizedProfit + unrealizedProfit;

			double totalCost = ProfitCalculator.totalCostForBuyTransactions(transactions);
			double worthRemainingTokens = amountOfRemainingTokens * price;
			double netProfit = worthRemainingTokens - totalProfit;

			String buyVolumeInfo = currencyFormatter.format(ProfitCalculator.totalCostForBuyTransactions(transactions));
			String sellVolumeInfo = currencyFormatter.format(ProfitCalculator.totalCostForSellTransactions(transactions));
			String remainingCostInfo = currencyFormatter.format(ProfitCalculator.remainingTokensCost(transactions));

			// COLOR:
			//  - Total Profit (green)
			//  - Total Cost (green)
			//  - Amount Tokens left (blue)
			//  - Worth remaining tokens (green)

			NumericValueParagraph costParagraph = new NumericValueParagraph(totalCost, currencyFormatter, true);
			NumericValueParagraph profitParagraph = new NumericValueParagraph(totalProfit, currencyFormatter, true);

			Container tokensLeftContainer = Container.builder("centered-row")
					.addComponent(() -> new ComponentBuilder<>(Paragraph.class)
							.addClass("asset-amount")
							.setStyle("margin-right", "2px")
							.setText(amountFormatter.format(amountOfRemainingTokens)).build()
					)
					.addComponent(new Span(symbol))
					.build();
			ProfitStatsDisplay tokensLeft = new ProfitStatsDisplay("Amount of tokens left:", tokensLeftContainer);

			resultsContainer.removeAll();
			resultsContainer.add(
					new ProfitStatsDisplay("Avg Buy:", avgBuy, currencyFormatter),
					new ProfitStatsDisplay("Avg Sell:", avgSell, currencyFormatter),
					new ProfitStatsDisplay("Avg Growth Rate", percentageFormatter.format(ProfitUtils.growthPercentage(avgBuy, avgSell))),

					new Hr(),
					new ProfitStatsDisplay("Total Cost", totalCost, currencyFormatter, true),

					new Hr(),
					tokensLeft,
					new ProfitStatsDisplay("Worth of remaining tokens:", worthRemainingTokens, currencyFormatter),
					new ProfitStatsDisplay("Cost for remaining tokens:", remainingCostInfo),

					new Hr(),
					new ProfitStatsDisplay("Realized Profit", realizedProfit, currencyFormatter),
					new ProfitStatsDisplay("Unrealized Profit", unrealizedProfit, currencyFormatter),
					new ProfitStatsDisplay("Total Profit", totalProfit, currencyFormatter, true),
//                    new ProfitStatsDisplay("Net Profit", currencyFormatter.format(netProfit)), // FIXME: DOESN'T DISPLAY RIGHT VALUES

					new Hr(),
					new ProfitStatsDisplay("Buy Trading Volume", buyVolumeInfo),
					new ProfitStatsDisplay("Sell Trading Volume", sellVolumeInfo)
			);

			metadataDetailsContainer.removeAll();
			metadataDetailsContainer.add(createTable(assetSymbolField.getSelectedAsset(), avgBuy, avgSell));
		});
		button.addClassName("add-entity-btn");
		return button;
	}

	private void updateVisibilityForMetaData(Asset asset) {
		metadataDetailsContainer.setVisible(asset != null);
	}

	private Div statsItem(String labelText, double value) {
		return new Div(new Paragraph(labelText), new Paragraph(String.valueOf(value)));
	}

	private void createNewLayout() {
		TransactionalLayout newLayout = new TransactionalLayout(portfolio, instrumentsFacadeService, portfolioPerformanceTracker);
		newLayout.addClassName("buy-sell-layout");

		MonoIcon deleteBtn = PictogramIcon.TRASH_CAN_OUTLINE.create();
		deleteBtn.addClickListener(e -> {
			transactionalLayouts.remove(newLayout);
			newLayout.removeFromParent();
		});

		newLayout.add(deleteBtn);
		transactionalLayouts.add(newLayout);
		layoutsContainer.add(newLayout);
	}

	private List<Transaction> getListOfTransactions() {
		return transactionalLayouts.stream()
				.map(layout -> {
					Asset selectedAsset = assetSymbolField.getSelectedAsset();
					double marketPrice = layout.getMarketPriceField().doubleValue();
					double orderTotalCost = layout.getTotalCostField().doubleValue();
					TransactionType type = layout.getTypeField().getValue();

					return new Transaction(selectedAsset, marketPrice, orderTotalCost, type);
				}).toList();
	}

	// TODO: Update this
	private Html createTable(Asset asset, double averageBuyPrice, double averageSellPrice) {
		double currentPrice = asset.getMarketPrice();

		BigInteger totalMarketSupply = asset.getTotalSupply();
		double currentFDV = ProfitUtils.fdv(totalMarketSupply, currentPrice);
		double avgBuyFDV = ProfitUtils.fdv(totalMarketSupply, averageBuyPrice);
		double avgSellFDV = ProfitUtils.fdv(totalMarketSupply, averageSellPrice);

		BigInteger circulationSupply = asset.getCirculationSupply();
		double currentMarketCap = ProfitUtils.marketCap(circulationSupply, currentPrice);
		double avgBuyMarketCap = ProfitUtils.marketCap(circulationSupply, averageBuyPrice);
		double avgSellMarketCap = ProfitUtils.marketCap(circulationSupply, averageSellPrice);

		return new Html(MessageFormat.format("""
						<table class="inside-border">
						  <tr>
						    <td></td>
						    <td>Current</td>
						    <td>Avg Buy</td>
						    <td>Avg Sell</td>
						  </tr>
						  <tr>
						    <td>Price</td>
						    <td>{0}</td>
						    <td>{1}</td>
						    <td>{2}</td>
						  </tr>
						  <tr>
						    <td>Market Cap</td>
						    <td>{3}</td>
						    <td>{4}</td>
						    <td>{5}</td>
						  </tr>
						  <tr>
						    <td>FDV</td>
						    <td>{6}</td>
						    <td>{7}</td>
						    <td>{8}</td>
						  </tr>
						</table>
						""",
				currencyFormatter.format(currentPrice), currencyFormatter.format(averageBuyPrice), currencyFormatter.format(averageSellPrice),
				compactFormatter.format(currentMarketCap), compactFormatter.format(avgBuyMarketCap), compactFormatter.format(avgSellMarketCap),
				compactFormatter.format(currentFDV), compactFormatter.format(avgBuyFDV), compactFormatter.format(avgSellFDV))
		);
	}

}


