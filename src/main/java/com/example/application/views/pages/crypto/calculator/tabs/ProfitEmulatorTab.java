package com.example.application.views.pages.crypto.calculator.tabs;

import com.example.application.data.dtos.AssetDTO;
import com.example.application.data.dtos.PortfolioDTO;
import com.example.application.data.dtos.TransactionDTO;
import com.example.application.entities.common.TransactionType;
import com.example.application.services.crypto.InstrumentsFacadeService;
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
import com.example.application.views.components.utils.HasNotifications;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.theme.lumo.LumoIcon;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
    TODO: [LONG-TERM] Work on styling table
        - https://colorlib.com/wp/css3-table-templates/
        - Fix table is not displayed

	TODO: [LONG-TERM] Add dropdown-details with stats component
	    - Posibility to open and close some details (as there are a lot of them)

* */

@Slf4j
public class ProfitEmulatorTab extends BaseCalculatorTab implements HasNotifications, BeforeEnterObserver {

	private final PortfolioDTO portfolio;
	private final InstrumentsFacadeService instrumentsFacadeService;

	private final AssetComboBox assetSymbolField;
	private final List<TransactionalLayout> transactionalLayouts = new ArrayList<>();
	private final Button addNewLayoutBtn = new Button("Add Transaction", LumoIcon.PLUS.create());
	private final Container layoutsContainer = new Container("layout-container");
	private final Container metadataDetailsContainer = new Container("profit-meta-data");

	private AssetDTO asset;
	private final Binder<SelectedAsset> binder = new Binder<>(SelectedAsset.class);

	public ProfitEmulatorTab(InstrumentsFacadeService instrumentsFacadeService)
	{
		super("Buy & Sell Emulator", instrumentsFacadeService);
		this.instrumentsFacadeService = instrumentsFacadeService;
		this.assetSymbolField = new AssetComboBox(instrumentsFacadeService);
		this.portfolio = instrumentsFacadeService.getActivePortfolio();
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		initializeBinder();
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		buildTab();
	}

	private void buildTab() {
		initializeFields();
	}

	private void initializeFields() {
		assetSymbolField.addValueChangeListener(field -> {
			if (field.getValue() == null) {
				return;
			}

			AssetDTO selectedAsset = field.getValue();
			transactionalLayouts.forEach(layout -> layout.setAsset(selectedAsset));
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

		TransactionalLayout defaultLayout = new TransactionalLayout(portfolio, instrumentsFacadeService);
		defaultLayout.addClassName("buy-sell-layout");
		transactionalLayouts.add(defaultLayout);
		layoutsContainer.add(defaultLayout);
		add(metadataDetailsContainer);

		return new Div(assetSymbolField, layoutsContainer, addNewLayoutBtn);
	}

	@Override
	protected Button createDisplayResultsBtn() {
		Button button = new Button("Calculate", event -> {
			if (binder.validate().hasErrors() || isAnyLayoutInvalid()) {
				showErrorNotification("Please fill all the input fields correctly");
				return;
			}

			try {
				displayResults();
			} catch (IllegalArgumentException e) {
				showErrorNotification("ERROR: The sell amount is more than buy amount");
			} catch (Exception e) {
				showErrorNotification("ERROR: Something wrong happend. Please share the steps");
			}
		});
		button.addClassName("add-entity-btn");
		return button;
	}

	private void displayResults() {
		String symbol = assetSymbolField.getSymbol().orElse("");
		BigDecimal marketPrice = assetSymbolField.getMarketPrice().orElse(BigDecimal.ZERO);
		List<TransactionDTO> transactions = getListOfTransactions();

		BigDecimal price = assetSymbolField.getSelectedAsset().getMarketPrice();
		BigDecimal avgBuy = ProfitCalculator.averageBuyPrice(transactions);
		BigDecimal avgSell = ProfitCalculator.averageSellPrice(transactions);
		BigDecimal amountOfRemainingTokens = ProfitCalculator.getAmountOfRemainingTokens(transactions);
		BigDecimal realizedProfit = ProfitCalculator.realizedProfit(transactions);
		BigDecimal unrealizedProfit = amountOfRemainingTokens.multiply(marketPrice);
		BigDecimal totalProfit = realizedProfit.add(unrealizedProfit);

		BigDecimal totalCost = ProfitCalculator.totalCostForBuyTransactions(transactions);
		BigDecimal worthRemainingTokens = amountOfRemainingTokens.multiply(price);
		BigDecimal netProfit = worthRemainingTokens.subtract(totalProfit);

		String buyVolumeInfo = currencyFormatter.format(ProfitCalculator.totalCostForBuyTransactions(transactions));
		String sellVolumeInfo = currencyFormatter.format(ProfitCalculator.totalCostForSellTransactions(transactions));
		String remainingCostInfo = currencyFormatter.format(ProfitCalculator.remainingTokensCost(transactions));

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
	}

	private void updateVisibilityForMetaData(AssetDTO asset) {
		metadataDetailsContainer.setVisible(asset != null);
	}

	private Div statsItem(String labelText, double value) {
		return new Div(new Paragraph(labelText), new Paragraph(String.valueOf(value)));
	}

	private void createNewLayout() {
		TransactionalLayout newLayout = new TransactionalLayout(portfolio, instrumentsFacadeService);
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

	private boolean isAnyLayoutInvalid() {
		return transactionalLayouts.stream()
				.map(TransactionalLayout::isValid)
				.collect(Collectors.toSet())
				.contains(false);
	}

	private List<TransactionDTO> getListOfTransactions() {
		return transactionalLayouts.stream()
				.map(layout -> {
					AssetDTO selectedAsset = assetSymbolField.getSelectedAsset();
					BigDecimal marketPrice = layout.getTransactionInput().getPrice();
					BigDecimal orderQuantity = layout.getTransactionInput().getAmount();
					BigDecimal orderTotalCost = layout.getTransactionInput().getTotalCost();
					TransactionType type = layout.getTransactionInput().getType();

					TransactionDTO dto = new TransactionDTO();
					dto.setAssetSymbol(selectedAsset.getSymbol());
					dto.setMarketPrice(marketPrice);
					dto.setOrderQuantity(orderQuantity);
					dto.setType(type);
					return dto;
				}).toList();
	}

	private Html createTable(AssetDTO asset, BigDecimal averageBuyPrice, BigDecimal averageSellPrice) {
		BigDecimal currentPrice = asset.getMarketPrice();

		BigInteger totalMarketSupply = asset.getTotalSupply();
		BigDecimal currentFDV = ProfitUtils.fdv(totalMarketSupply, currentPrice);
		BigDecimal avgBuyFDV = ProfitUtils.fdv(totalMarketSupply, averageBuyPrice);
		BigDecimal avgSellFDV = ProfitUtils.fdv(totalMarketSupply, averageSellPrice);

		BigInteger circulationSupply = asset.getCirculationSupply();
		BigDecimal currentMarketCap = ProfitUtils.marketCap(circulationSupply, currentPrice);
		BigDecimal avgBuyMarketCap = ProfitUtils.marketCap(circulationSupply, averageBuyPrice);
		BigDecimal avgSellMarketCap = ProfitUtils.marketCap(circulationSupply, averageSellPrice);

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

	private void initializeBinder() {
		assetSymbolField.setRequiredIndicatorVisible(true);

		binder.forField(assetSymbolField)
				.asRequired("Please select an asset")
				.withValidationStatusHandler(status -> {
					assetSymbolField.setInvalid(status.isError());
					assetSymbolField.setErrorMessage(
							status.getMessage().orElse(null)
					);
				})
				.bind(SelectedAsset::getAsset, SelectedAsset::setAsset);

		binder.setBean(new SelectedAsset());
	}

	@Data
	private static class SelectedAsset {
		private AssetDTO asset;
	}

}
