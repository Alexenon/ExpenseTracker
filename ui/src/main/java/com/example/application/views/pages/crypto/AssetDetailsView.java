package com.example.application.views.pages.crypto;

import com.example.application.InstrumentsFacadeService;
import com.example.application.PortfolioPerformanceTracker;
import com.example.application.asset.AssetDTO;
import com.example.application.portfolio.PortfolioDTO;
import com.example.application.transaction.TransactionType;
import com.example.application.utils.FinancialConstants;
import com.example.application.utils.formatters.AmountFormatter;
import com.example.application.utils.formatters.CommonFormatters;
import com.example.application.utils.formatters.CompactFormatter;
import com.example.application.utils.lang.MathUtils;
import com.example.application.utils.lang.StringUtils;
import com.example.application.views.components.PriceChangeNotifier;
import com.example.application.views.components.PriceUpdatable;
import com.example.application.views.components.PriceWatchlistComponent;
import com.example.application.views.components.TransactionsGrid;
import com.example.application.views.components.core.ComponentBuilder;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.dialogs.transactions.AddTransactionDialog;
import com.example.application.views.components.custom.dialogs.transactions.TransactionCreatedOrUpdatedEvent;
import com.example.application.views.components.custom.display.NumericValueParagraph;
import com.example.application.views.components.custom.fields.AmountField;
import com.example.application.views.components.custom.fields.MoneyField;
import com.example.application.views.components.custom.fields.PricePercentageWrapper;
import com.example.application.views.components.custom.fields.stats.PortfolioStatsDisplay;
import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.DefaultPage;
import com.example.application.views.pages.RebuildablePage;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.ScrollOptions;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoIcon;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Objects;

/*
    FIXME: When page is loaded, it scroll to the PriceWatchlistComponent
*/

@Slf4j
@PermitAll
@PageTitle("Asset Details")
@Route(value = "asset", layout = MainLayout.class)
public class AssetDetailsView extends DefaultPage implements HasUrlParameter<String>, RebuildablePage, BeforeEnterObserver, BeforeLeaveObserver, PriceUpdatable {

	private final PriceChangeNotifier priceChangeNotifier;
	private final InstrumentsFacadeService instrumentsFacadeService;
	private final PortfolioPerformanceTracker portfolioPerformanceTracker;

	private final UI ui;
	private String assetSymbol;
	private AssetDTO asset;
	private PortfolioDTO portfolio;

	@Autowired
	public AssetDetailsView(InstrumentsFacadeService instrumentsFacadeService,
							PortfolioPerformanceTracker portfolioPerformanceTracker,
							PriceChangeNotifier priceChangeNotifier)
	{
		this.instrumentsFacadeService = instrumentsFacadeService;
		this.portfolioPerformanceTracker = portfolioPerformanceTracker;
		this.priceChangeNotifier = priceChangeNotifier;
		this.ui = UI.getCurrent();
	}

	@Override
	public void setParameter(BeforeEvent beforeEvent, String assetSymbol) {
		this.assetSymbol = assetSymbol;
		this.portfolio = instrumentsFacadeService.getActivePortfolio();
		initializePage();
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		buildPage();
		priceChangeNotifier.addObserver(ui, this);
	}

	@Override
	public void beforeLeave(BeforeLeaveEvent event) {
		priceChangeNotifier.removeObserver(ui);
	}

	@Override
	public void initializePage() {
		setClassName("coin-details-content");
		ComponentUtil.addListener(UI.getCurrent(), TransactionCreatedOrUpdatedEvent.class, event -> rebuildPage());
	}

	@Override
	public void buildPage() {
		asset = instrumentsFacadeService.getAssetBySymbol(assetSymbol)
				.orElseThrow(() -> new NullPointerException("There is no such asset as %s".formatted(asset.getSymbol())));

		add(
				headerDetailsSection(),
				holdingsSection(),
				notesAndConvertorSection(),
				watchlistSection(),
				marketStatsSection(),
				aboutSection(),
				transactionHistorySection()
		);
		scrollTopOfThePage();
	}

	@Override
	public void update() {
		rebuildPage();
	}

	@Override
	public void rebuildPage() {
		ui.access(() -> {
			removeAll();
			buildPage();
		});
	}

	private Section headerDetailsSection() {
		Section section = new Section();
		Paragraph rank = new ComponentBuilder<>(Paragraph.class)
				.setText("RANK #4")
				.addClass("coin-overview-rank")
				.build();

		Container coinNameContainer = Container.builder("coin-overview-name-container")
				.addComponent(() -> {
					Image image = new Image(asset.getImageUrl(), asset.getSymbol());
					image.setClassName("coin-overview-image");
					return image;
				})
				.addComponent(new H1(asset.getFullName()))
				.addComponent(() -> new ComponentBuilder<>(Span.class).setText("•").addClass("dot").build())
				.addComponent(new Span(asset.getSymbol()))
				.build();

		BigDecimal price = asset.getMarketPrice();
		BigDecimal percentage = asset.getChangePercentage();
		PricePercentageWrapper priceWrapper = new PricePercentageWrapper(price, percentage);
		priceWrapper.addClassName("price-wrapper");

		Div coinInfoContainer = new Div(rank, coinNameContainer, priceWrapper);
		Button markAsFavorite = new Button(getStarIcon(isAssetMarkedAsFavorite()));
		markAsFavorite.addClassName("rounded-button");
		markAsFavorite.addClickListener(e -> {
			boolean isFavorite = isAssetMarkedAsFavorite();
			instrumentsFacadeService.updateMarkAssetAsFavorite(asset.getSymbol(), !isFavorite);
			markAsFavorite.setIcon(getStarIcon(!isFavorite));
		});

		section.addClassName("asset-details-header");
		section.add(coinInfoContainer, markAsFavorite);

		return section;
	}

	private boolean isAssetMarkedAsFavorite() {
		return instrumentsFacadeService.isAssetMarkedAsFavorite(asset.getSymbol());
	}

	private Section notesAndConvertorSection() {
		Section section = new Section();
		section.addClassName("notes-convertor-section");
		section.add(notesSection());
		section.add(convertorSection());
		return section;
	}

	private Section notesSection() {
		H3 title = new H3("Notes");
		title.setClassName("section-title");
		TextArea notesArea = new TextArea();
		notesArea.setClassName("note-area");
		notesArea.setPlaceholder("Add your thoughts about coin here.");
		String comment = Objects.requireNonNullElse(instrumentsFacadeService.getAssetComment(asset.getSymbol()), "");
		notesArea.setValue(comment);
		Button saveBtn = new Button("Save");
		saveBtn.addClickListener(l -> {
			try {
				instrumentsFacadeService.updateAssetComment(asset.getSymbol(), notesArea.getValue());
				showSuccessfulNotification("Succesfully saved asset note");
			} catch (Exception e) {
				showErrorNotification("Something went wrong: " + e.getLocalizedMessage());
			}
		});

		Container sectionBody = Container.builder()
				.addClassNames("note-wrapper")
				.addComponent(notesArea)
				.addComponent(saveBtn)
				.build();

		Section section = new Section(title, sectionBody);
		section.addClassName("notes-section");
		return section;
	}

	private Section convertorSection() {
		H3 title = new H3("Crypto Convertor");
		title.setClassName("section-title");

		Image inputImage = new Image(asset.getImageUrl(), asset.getSymbol());
		inputImage.setClassName("coin-overview-image");

		AmountField tokenAmountField = new AmountField();
		tokenAmountField.setValue("1");

		MoneyField moneyField = new MoneyField();
		moneyField.setPrefix(false);
		moneyField.setValue(asset.getMarketPrice());

		Container inputContainer = Container.builder()
				.addComponent(inputImage)
				.addComponent(new Paragraph(asset.getSymbol()))
				.addComponent(tokenAmountField)
				.build();

		Container outputContainer = Container.builder()
				.addComponent(() -> {
					Image outputImage = new Image("./images/others/usd.png", "USD image");
					outputImage.setWidth("30px");
					outputImage.setHeight("30px");
					outputImage.getStyle().set("margin", "0 17px 0 5px");
					return outputImage;
				})
				.addComponent(new Paragraph("USD"))
				.addComponent(moneyField)
				.build();

		Container sectionBody = Container.builder()
				.addClassName("convertor")
				.addComponent(inputContainer)
				.addComponent(outputContainer)
				.build();

		tokenAmountField.setValueChangeMode(ValueChangeMode.EAGER);
		tokenAmountField.addKeyUpListener(e -> {
			BigDecimal calculatedPrice = tokenAmountField.getAmount().multiply(asset.getMarketPrice());
			moneyField.setValue(calculatedPrice);
		});

		moneyField.setValueChangeMode(ValueChangeMode.EAGER);
		moneyField.addKeyUpListener(e -> {
			BigDecimal moneyAmount = moneyField.getMoneyAmount();
			BigDecimal price = asset.getMarketPrice();
			BigDecimal amountOfTokens = price.signum() == 0
					? BigDecimal.ZERO
					: moneyAmount.divide(price, FinancialConstants.PRICE_SCALE, RoundingMode.HALF_UP);
			tokenAmountField.setValue(amountOfTokens);
		});

		Section section = new Section(title, sectionBody);
		section.addClassName("convertor-section");
		return section;
	}

	private Section holdingsSection() {
		Container header = Container.builder("section-header")
				.addComponent(() -> {
					H3 title = new H3("Holdings");
					title.setClassName("section-title");
					return title;
				})
				.addComponent(() -> {
					Button addHoldingBtn = new Button("Add Holding", LumoIcon.PLUS.create());
					addHoldingBtn.setIconAfterText(false);
					addHoldingBtn.addClickListener(e -> new AddTransactionDialog(portfolio, asset, instrumentsFacadeService).open());
					return addHoldingBtn;
				})
				.build();

		BigDecimal assetCost = portfolioPerformanceTracker.getAssetRemainingTokensCost(portfolio, asset).orElse(BigDecimal.ZERO);
		BigDecimal assetWorth = portfolioPerformanceTracker.getAssetWorth(portfolio, asset);
		BigDecimal assetProfitLoss = portfolioPerformanceTracker.getAssetTotalProfit(portfolio, asset);
		BigDecimal assetRealized = portfolioPerformanceTracker.getAssetRealizedProfit(portfolio, asset).orElse(BigDecimal.ZERO);
		BigDecimal assetUnrealized = portfolioPerformanceTracker.getAssetUnrealizedProfit(portfolio, asset);
		BigDecimal profitLossPercentage = portfolioPerformanceTracker.getAssetNetProfitPercentage(portfolio, asset);
		int assetDiversityPercentage = portfolioPerformanceTracker.getAssetDiversityPercentage(portfolio, asset);

		NumericValueParagraph costValue = new NumericValueParagraph(assetCost, CommonFormatters.CURRENCY);
		NumericValueParagraph worthValue = new NumericValueParagraph(assetWorth, CommonFormatters.CURRENCY);
		PricePercentageWrapper profitLossContainer = new PricePercentageWrapper(assetProfitLoss, profitLossPercentage);
		profitLossContainer.setPercentageBadgeBackground(false);

		String ratio = portfolioPerformanceTracker.getAssetBuySellRatio(portfolio, asset);
		String[] ratioParts = ratio.split(":");
		String avgTimeHolding = String.format("%.1f days", portfolioPerformanceTracker.getAssetHoldingDays(portfolio, asset));
		String tokensAmount = AmountFormatter.withDefaults().format(instrumentsFacadeService.getAmountOfTokens(portfolio.getId(), asset.getSymbol()));

		Div body = new Div();
		body.addClassName("section-card-wrapper");
		body.add(
				new PortfolioStatsDisplay("Tokens amount", tokensAmount),
				new PortfolioStatsDisplay("Total Worth", worthValue),
				new PortfolioStatsDisplay("Total Cost", costValue),
				new PortfolioStatsDisplay("Total Profit/Loss", profitLossContainer),
				new PortfolioStatsDisplay("Realized Profit", new NumericValueParagraph(assetRealized, CommonFormatters.CURRENCY)),
				new PortfolioStatsDisplay("Unrealized Profit", new NumericValueParagraph(assetUnrealized, CommonFormatters.CURRENCY)),
				new PortfolioStatsDisplay("Portfolio Diversity", getAssetDiversityContainer(BigDecimal.valueOf(assetDiversityPercentage))),
				new PortfolioStatsDisplay("Buy/Sell Ratio", ratio,
						String.format("%s%% of transactions are buys, %s%% are sells, in dollar equivalent", ratioParts[0].trim(), ratioParts[1])),
				new PortfolioStatsDisplay("Avg Holding Time", avgTimeHolding, "Average holding time from the first buy")
		);

		return new Section(header, body);
	}

	private Section marketStatsSection() {
		Section section = new Section();
		H3 title = new H3("Market Stats");
		title.setClassName("section-title");
		CompactFormatter compactFormatter = new CompactFormatter();

		BigInteger assetTotalMarketCap = asset.getTotalMarketCap();
		BigInteger assetTotalSupply = asset.getTotalSupply();
		BigInteger asset24HourVolume = asset.getTodayVolume();
		BigInteger circulationSupplyValue = asset.getCirculationSupply();
		int percentageUseOfCirculationSupply = MathUtils.percentageOf(circulationSupplyValue, assetTotalSupply).intValue();

		Container circulationSupplyContainer = Container.builder("portfolio-diversity")
				.addComponent(() -> {
					Paragraph circulationText = new Paragraph(compactFormatter.format(circulationSupplyValue.doubleValue()));
					Paragraph percentageText = new Paragraph(String.format("(%d%%)", percentageUseOfCirculationSupply));
					percentageText.getStyle().set("color", "blue");
					return new HorizontalLayout(circulationText, percentageText);
				})
				.addComponent(new ProgressBar(0, 100, percentageUseOfCirculationSupply))
				.build();

		Container sectionBody = Container.builder("section-card-wrapper", "market-stats-section")
				.addComponents(
						new PortfolioStatsDisplay("Market Cap", compactFormatter.format(assetTotalMarketCap.doubleValue())),
						new PortfolioStatsDisplay("Circulation Supply", circulationSupplyContainer),
						new PortfolioStatsDisplay("Total Supply", compactFormatter.format(assetTotalSupply.doubleValue())),
						new PortfolioStatsDisplay("Volume 24h", compactFormatter.format(asset24HourVolume))
				)
				.build();

		section.add(title, sectionBody);
		return section;
	}

	private Section aboutSection() {
		H3 title = new H3("About " + asset.getFullName());
		title.setClassName("section-title");
		Paragraph description = new Paragraph(asset.getDescription());
		Container body = new Container("section-card-wrapper", description);
		return new Section(title, body);
	}

	private Icon getStarIcon(boolean isMarkedAsFavorite) {
		return isMarkedAsFavorite ? VaadinIcon.STAR.create() : VaadinIcon.STAR_O.create();
	}

	private Section watchlistSection() {
		Section section = new Section(
				createWatchlistSection(TransactionType.BUY),
				createWatchlistSection(TransactionType.SELL)
		);
		section.addClassName("notes-convertor-section");
		return section;
	}

	private Div createWatchlistSection(TransactionType transactionType) {
		PriceWatchlistComponent watchlistComponent = new PriceWatchlistComponent(portfolio, asset, transactionType, instrumentsFacadeService);

		Container header = Container.builder("section-header")
				.addComponent(() -> {
					H3 title = new H3(StringUtils.uppercaseFirstLetter(transactionType.name()) + " Watchlist");
					title.setClassName("section-title");
					return title;
				})
				.addComponent(() -> {
					Button addWatchlistBtn = new Button("Add Watchlist", LumoIcon.PLUS.create());
					addWatchlistBtn.setIconAfterText(false);
					addWatchlistBtn.addClickListener(e -> {
						watchlistComponent.addNewPriceLayout();
						ScrollOptions scrollOptions = new ScrollOptions();
						scrollOptions.setBehavior(ScrollOptions.Behavior.SMOOTH);
						scrollOptions.setBlock(ScrollOptions.Alignment.END);
						watchlistComponent.scrollIntoView(scrollOptions);
					});
					return addWatchlistBtn;
				})
				.build();

		Container body = Container.builder("section-card-wrapper")
				.addComponent(watchlistComponent)
				.build();

		return new Container("watchlist", header, body);
	}

	private Section transactionHistorySection() {
		TransactionsGrid transactionsGrid = new TransactionsGrid(instrumentsFacadeService);
		transactionsGrid.setItems(instrumentsFacadeService.getTransactionsByAsset(portfolio.getId(), asset.getSymbol()));
		transactionsGrid.setPageSize(10);

		H3 title = new H3("Transactions");
		title.setClassName("section-title");

		Button addTransactionBtn = new Button("Add Transaction", LumoIcon.PLUS.create());
		addTransactionBtn.setIconAfterText(false);
		addTransactionBtn.addClickListener(e -> {
			new AddTransactionDialog(portfolio, asset, instrumentsFacadeService).open();
			transactionsGrid.setItems(instrumentsFacadeService.getTransactionsByAsset(portfolio.getId(), asset.getSymbol()));
		});

		Button seeAllTransactionsBtn = new Button("See all transactions");
		Container buttonsContainer = new Container("header-buttons", addTransactionBtn, seeAllTransactionsBtn);
		Container header = new Container("section-header", title, buttonsContainer);

		return new Section(header, transactionsGrid);
	}

	private Container getAssetDiversityContainer(BigDecimal assetDiversityPercentage) {
		double percentageValue = assetDiversityPercentage
				.setScale(2, RoundingMode.HALF_UP)
				.doubleValue();

		return Container.builder("portfolio-diversity")
				.addComponent(() -> {
					NumericValueParagraph p = new NumericValueParagraph(assetDiversityPercentage, CommonFormatters.PERCENTAGE);
					p.getStyle().setColor("blue");
					return p;
				})
				.addComponent(new ProgressBar(0, 100, percentageValue))
				.build();
	}

}