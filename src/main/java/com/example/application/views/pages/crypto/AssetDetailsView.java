package com.example.application.views.pages.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.entities.crypto.Portfolio;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.utils.common.formatters.CommonFormatters;
import com.example.application.utils.common.formatters.number.AmountFormatter;
import com.example.application.utils.common.formatters.number.CompactFormatter;
import com.example.application.utils.common.lang.MathUtils;
import com.example.application.utils.common.lang.StringUtils;
import com.example.application.views.components.PriceChangeNotifier;
import com.example.application.views.components.PriceChangeblePage;
import com.example.application.views.components.PriceWatchlistComponent;
import com.example.application.views.components.TransactionsGrid;
import com.example.application.views.components.core.ComponentBuilder;
import com.example.application.views.components.core.Container;
import com.example.application.views.components.custom.dialogs.transactions.AddTransactionDialog;
import com.example.application.views.components.custom.display.NumericValueParagraph;
import com.example.application.views.components.custom.fields.AmountField;
import com.example.application.views.components.custom.fields.CurrencyField;
import com.example.application.views.components.custom.fields.PricePercentageWrapper;
import com.example.application.views.components.custom.fields.stats.PortfolioStatsDisplay;
import com.example.application.views.layouts.MainLayout;
import com.example.application.views.pages.DefaultPage;
import com.example.application.views.pages.RebuildablePage;
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

import java.math.BigInteger;
import java.util.Objects;

/*
    FIXME: When page is loaded, it scroll to the PriceWatchlistComponent
*/

@Slf4j
@PermitAll
@PageTitle("Asset Details")
@Route(value = "asset", layout = MainLayout.class)
public class AssetDetailsView extends DefaultPage implements HasUrlParameter<String>, RebuildablePage, BeforeEnterObserver, BeforeLeaveObserver, PriceChangeblePage {

    private final Portfolio portfolio;
    private final PriceChangeNotifier priceChangeNotifier;
    private final InstrumentsFacadeService instrumentsFacadeService;
    private final PortfolioPerformanceTracker portfolioPerformanceTracker;
    private final AddTransactionDialog addTransactionDialog;

    private final UI ui;
    private String assetSymbol;
    private Asset asset;

    @Autowired
    public AssetDetailsView(Portfolio portfolio,
                            InstrumentsFacadeService instrumentsFacadeService,
                            PortfolioPerformanceTracker portfolioPerformanceTracker,
                            PriceChangeNotifier priceChangeNotifier)
    {
        this.portfolio = portfolio;
        this.instrumentsFacadeService = instrumentsFacadeService;
        this.portfolioPerformanceTracker = portfolioPerformanceTracker;
        this.priceChangeNotifier = priceChangeNotifier;
        this.addTransactionDialog = new AddTransactionDialog(portfolio, instrumentsFacadeService);
        this.ui = UI.getCurrent();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String assetSymbol) {
        this.assetSymbol = assetSymbol;
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
    }

    @Override
    public void buildPage() {
        asset = instrumentsFacadeService.getAssetBySymbol(assetSymbol)
                .orElseThrow(() -> new NullPointerException("There is no such asset as %s".formatted(asset.getSymbol())));

        addTransactionDialog.setAsset(asset);
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
    public void updatePage() {
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

        double price = asset.getMarketPrice();
        double percentage = asset.getChangePercentage();
        PricePercentageWrapper priceWrapper = new PricePercentageWrapper(price, percentage);
        priceWrapper.addClassName("price-wrapper");

        Div coinInfoContainer = new Div(rank, coinNameContainer, priceWrapper);
        Button markAsFavorite = new Button(getStarIcon(isAssetMarkedAsFavorite()));
        markAsFavorite.addClassName("rounded-button");
        markAsFavorite.addClickListener(e -> {
            boolean isFavorite = isAssetMarkedAsFavorite();
            instrumentsFacadeService.updateMarkAssetAsFavorite(asset, !isFavorite);
            markAsFavorite.setIcon(getStarIcon(!isFavorite));
        });

        section.addClassName("asset-details-header");
        section.add(coinInfoContainer, markAsFavorite);

        return section;
    }

    private boolean isAssetMarkedAsFavorite() {
        return instrumentsFacadeService.isAssetMarkedAsFavorite(asset);
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
        String comment = Objects.requireNonNullElse(instrumentsFacadeService.getAssetComment(asset), "");
        notesArea.setValue(comment);
        Button saveBtn = new Button("Save");
        saveBtn.addClickListener(l -> {
            try {
                instrumentsFacadeService.updateAssetComment(asset, notesArea.getValue());
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
        tokenAmountField.setValue(1);

        CurrencyField usdAmountField = new CurrencyField();
        usdAmountField.setPrefix(false);
        usdAmountField.setValue(asset.getMarketPrice());

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
                .addComponent(usdAmountField)
                .build();

        Container sectionBody = Container.builder()
                .addClassName("convertor")
                .addComponent(inputContainer)
                .addComponent(outputContainer)
                .build();

        tokenAmountField.setValueChangeMode(ValueChangeMode.EAGER);
        tokenAmountField.addKeyUpListener(e -> {
            double calculatedPrice = tokenAmountField.doubleValue() * asset.getMarketPrice();
            usdAmountField.setValue(calculatedPrice);
        });

        usdAmountField.setValueChangeMode(ValueChangeMode.EAGER);
        usdAmountField.addKeyUpListener(e -> {
            double amount = usdAmountField.doubleValue();
            double price = asset.getMarketPrice();
            tokenAmountField.setValue(MathUtils.safeDivision(amount, price));
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
                    addHoldingBtn.addClickListener(e -> addTransactionDialog.open());
                    return addHoldingBtn;
                })
                .build();

        double assetCost = portfolioPerformanceTracker.getAssetRemainingTokensCost(portfolio, asset);
        double assetWorth = portfolioPerformanceTracker.getAssetWorth(portfolio, asset);
        double assetProfitLoss = portfolioPerformanceTracker.getAssetTotalProfit(portfolio, asset);
        double assetRealized = portfolioPerformanceTracker.getAssetRealizedProfit(portfolio, asset);
        double assetUnrealized = portfolioPerformanceTracker.getAssetUnrealizedProfit(portfolio, asset);
        double profitLossPercentage = portfolioPerformanceTracker.getAssetNetProfitPercentage(portfolio, asset);
        int assetDiversityPercentage = portfolioPerformanceTracker.getAssetDiversityPercentage(portfolio, asset);

        NumericValueParagraph costValue = new NumericValueParagraph(assetCost, CommonFormatters.CURRENCY);
        NumericValueParagraph worthValue = new NumericValueParagraph(assetWorth, CommonFormatters.CURRENCY);
        PricePercentageWrapper profitLossContainer = new PricePercentageWrapper(assetProfitLoss, profitLossPercentage);
        profitLossContainer.setPercentageBadgeBackground(false);

        String ratio = portfolioPerformanceTracker.getAssetBuySellRatio(portfolio, asset);
        String[] ratioParts = ratio.split(":");
        String avgTimeHolding = String.format("%.1f days", portfolioPerformanceTracker.getAssetHoldingDays(portfolio, asset));
        String tokensAmount = AmountFormatter.withDefaults().format(instrumentsFacadeService.getAmountOfTokens(portfolio, asset), asset);

        Div body = new Div();
        body.addClassName("section-card-wrapper");
        body.add(
                new PortfolioStatsDisplay("Tokens amount", tokensAmount),
                new PortfolioStatsDisplay("Total Worth", worthValue),
                new PortfolioStatsDisplay("Total Cost", costValue),
                new PortfolioStatsDisplay("Total Profit/Loss", profitLossContainer),
                new PortfolioStatsDisplay("Realized Profit", new NumericValueParagraph(assetRealized, CommonFormatters.CURRENCY)),
                new PortfolioStatsDisplay("Unrealized Profit", new NumericValueParagraph(assetUnrealized, CommonFormatters.CURRENCY)),
                new PortfolioStatsDisplay("Portfolio Diversity", getAssetDiversityContainer(assetDiversityPercentage)),
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
        double asset24HourVolume = asset.getTodayVolume();
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
        Paragraph description = new Paragraph(asset.getSummaryDescription());
        Container body = new Container("section-card-wrapper", description);
        return new Section(title, body);
    }

    private Icon getStarIcon(boolean isMarkedAsFavorite) {
        return isMarkedAsFavorite ? VaadinIcon.STAR.create() : VaadinIcon.STAR_O.create();
    }

    private Section watchlistSection() {
        Section section = new Section(
                createWatchlistSection(AssetWatcher.ActionType.BUY),
                createWatchlistSection(AssetWatcher.ActionType.SELL)
        );
        section.addClassName("notes-convertor-section");
        return section;
    }

    private Div createWatchlistSection(AssetWatcher.ActionType actionType) {
        PriceWatchlistComponent watchlistComponent = new PriceWatchlistComponent(portfolio, asset, actionType, instrumentsFacadeService);

        Container header = Container.builder("section-header")
                .addComponent(() -> {
                    H3 title = new H3(StringUtils.uppercaseFirstLetter(actionType.name()) + " Watchlist");
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
        transactionsGrid.setItems(instrumentsFacadeService.getTransactionsByAsset(portfolio, asset));
        transactionsGrid.setPageSize(10);
        transactionsGrid.addUpdateItemListener(l -> rebuildPage());

        H3 title = new H3("Transactions");
        title.setClassName("section-title");

        Button addTransactionBtn = new Button("Add Transaction", LumoIcon.PLUS.create());
        addTransactionBtn.setIconAfterText(false);
        addTransactionBtn.addClickListener(e -> {
            addTransactionDialog.open();
            transactionsGrid.setItems(instrumentsFacadeService.getTransactionsByAsset(portfolio, asset));
        });
        Button seeAllTransactionsBtn = new Button("See all transactions");
        Container buttonsContainer = new Container("header-buttons", addTransactionBtn, seeAllTransactionsBtn);

        Container header = new Container("section-header", title, buttonsContainer);

        return new Section(header, transactionsGrid);
    }

    private Container getAssetDiversityContainer(int assetDiversityPercentage) {
        return Container.builder("portfolio-diversity")
                .addComponent(() -> {
                    NumericValueParagraph p = new NumericValueParagraph(assetDiversityPercentage, CommonFormatters.PERCENTAGE);
                    p.getStyle().setColor("blue");
                    return p;
                })
                .addComponent(new ProgressBar(0, 100, assetDiversityPercentage))
                .build();
    }

}