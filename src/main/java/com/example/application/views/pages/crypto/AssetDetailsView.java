package com.example.application.views.pages.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.utils.common.MathUtils;
import com.example.application.utils.common.StringUtils;
import com.example.application.utils.common.number.AmountFormatter;
import com.example.application.utils.common.number.CompactFormatter;
import com.example.application.utils.common.number.CurrencyFormatter;
import com.example.application.utils.common.number.PercentageFormatter;
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
import com.vaadin.flow.component.ScrollOptions;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoIcon;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.Objects;

/*
    TODO: Add @Slf4j annotation with logs
    FIXME: When page is loaded, it scroll to the PriceWatchlistComponent
*/

@Slf4j
@PermitAll
@PageTitle("Asset Details")
@Route(value = "asset", layout = MainLayout.class)
public class AssetDetailsView extends DefaultPage implements HasUrlParameter<String> {

    private static final CurrencyFormatter currencyFormatter = CurrencyFormatter.withDefaults();
    private static final PercentageFormatter percentageFormatter = PercentageFormatter.withDefaults();

    @Autowired
    private InstrumentsFacadeService instrumentsFacadeService;
    @Autowired
    private PortfolioPerformanceTracker portfolioPerformanceTracker;

    private Asset asset;
    private AddTransactionDialog addTransactionDialog;

    @Override
    public void setParameter(BeforeEvent beforeEvent, String symbol) {
        this.asset = Objects.requireNonNull(instrumentsFacadeService.getAssetBySymbol(symbol));
        this.addTransactionDialog = new AddTransactionDialog(instrumentsFacadeService);
    }

    @Override
    protected void initializePage() {
        setClassName("coin-details-content");
        addTransactionDialog.setAsset(asset);
        scrollTopPage();
    }

    @Override
    protected void buildPage() {
        add(
                headerDetailsSection(),
                holdingsSection(),
                notesAndConvertorSection(),
                watchlistSection(),
                marketStatsSection(),
                aboutSection(),
                transactionHistorySection()
        );
    }

    private Section headerDetailsSection() {
        Section section = new Section();
        Paragraph rank = new Paragraph("RANK #4");
        rank.setClassName("coin-overview-rank");

        Container coinNameContainer = Container.builder("coin-overview-name-container")
                .addComponent(() -> {
                    Image image = new Image(instrumentsFacadeService.getAssetImgUrl(asset), asset.getSymbol());
                    image.setClassName("coin-overview-image");
                    return image;
                })
                .addComponent(new H1(asset.getFullName()))
                .addComponent(() -> new ComponentBuilder<>(Span.class).setText("•").addClass("dot").build())
                .addComponent(new Span(asset.getSymbol()))
                .build();

        double price = instrumentsFacadeService.getAssetMarketPrice(asset);
        double percentage = instrumentsFacadeService.getAsset24HourChangePercentage(asset);
        PricePercentageWrapper priceWrapper = new PricePercentageWrapper(price, percentage);
        priceWrapper.addClassName("price-wrapper");

        Div coinInfoContainer = new Div(rank, coinNameContainer, priceWrapper);

        Button markAsFavorite = new Button(getStarIcon(asset.isMarkedAsFavorite()));
        markAsFavorite.addClassName("rounded-button");
        markAsFavorite.addClickListener(e -> {
            boolean isFavorite = asset.isMarkedAsFavorite();
            asset.setMarkedAsFavorite(!isFavorite);
            markAsFavorite.setIcon(getStarIcon(!isFavorite));
        });

        section.addClassName("asset-details-header");
        section.add(coinInfoContainer, markAsFavorite);

        return section;
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
        Button saveBtn = new Button("Save");

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

        Image inputImage = new Image(instrumentsFacadeService.getAssetImgUrl(asset), asset.getSymbol());
        inputImage.setClassName("coin-overview-image");

        AmountField tokenAmountField = new AmountField();
        tokenAmountField.setValue(1);

        CurrencyField usdAmountField = new CurrencyField();
        usdAmountField.setValue(instrumentsFacadeService.getAssetMarketPrice(asset));

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
            double calculatedPrice = tokenAmountField.doubleValue() * instrumentsFacadeService.getAssetMarketPrice(asset);
            usdAmountField.setValue(calculatedPrice);
        });

        usdAmountField.setValueChangeMode(ValueChangeMode.EAGER);
        usdAmountField.addKeyUpListener(e -> {
            double amount = usdAmountField.doubleValue();
            double price = instrumentsFacadeService.getAssetMarketPrice(asset);
            tokenAmountField.setValue(MathUtils.safeZeroDivision(amount, price));
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

        double assetCost = portfolioPerformanceTracker.getAssetRemainingTokensCost(asset);
        double assetWorth = portfolioPerformanceTracker.getAssetTotalWorth(asset);
        double assetProfitLoss = portfolioPerformanceTracker.getAssetTotalProfit(asset);
        double assetRealized = portfolioPerformanceTracker.getAssetRealizedProfit(asset);
        double assetUnrealized = portfolioPerformanceTracker.getAssetUnrealizedProfit(asset);
        double profitLossPercentage = portfolioPerformanceTracker.getAssetNetProfitPercentage(asset);
        int assetDiversityPercentage = portfolioPerformanceTracker.getAssetDiversityPercentage(asset);

        NumericValueParagraph costValue = new NumericValueParagraph(assetCost, currencyFormatter);
        NumericValueParagraph worthValue = new NumericValueParagraph(assetWorth, currencyFormatter);
        PricePercentageWrapper profitLossContainer = new PricePercentageWrapper(assetProfitLoss, profitLossPercentage);
        profitLossContainer.setPercentageBadgeBackground(false);

        String ratio = portfolioPerformanceTracker.getAssetBuySellRatio(asset);
        String[] ratioParts = ratio.split(":");
        String avgTimeHolding = String.format("%.1f days", portfolioPerformanceTracker.getAssetAverageHoldingDays(asset));
        String tokensAmount = AmountFormatter.withDefaults().format(instrumentsFacadeService.getAmountOfTokens(asset), asset);

        Div body = new Div();
        body.addClassName("section-card-wrapper");
        body.add(
                new PortfolioStatsDisplay("Tokens amount", tokensAmount),
                new PortfolioStatsDisplay("Total Worth", worthValue),
                new PortfolioStatsDisplay("Total Cost", costValue),
                new PortfolioStatsDisplay("Total Profit/Loss", profitLossContainer),
                new PortfolioStatsDisplay("Realized Profit", new NumericValueParagraph(assetRealized, currencyFormatter)),
                new PortfolioStatsDisplay("Unrealized Profit", new NumericValueParagraph(assetUnrealized, currencyFormatter)),
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

        double assetTotalMarketCap = instrumentsFacadeService.getAssetTotalMarketCap(asset);
        Div marketCap = new PortfolioStatsDisplay("Market Cap", compactFormatter.format(assetTotalMarketCap));

        BigInteger assetTotalSupply = instrumentsFacadeService.getAssetSupplyTotal(asset);
        double asset24HourVolume = instrumentsFacadeService.getAsset24HourVolume(asset);
        BigInteger circulationSupplyValue = instrumentsFacadeService.getAssetSupplyCirculating(asset);
        int percentageUseOfCirculationSupply = MathUtils.percentageOf(circulationSupplyValue, assetTotalSupply);
        ProgressBar bar = new ProgressBar(0, 100, percentageUseOfCirculationSupply);

        Container circulationSupplyContainer = Container.builder("portfolio-diversity")
                .addComponent(() -> {
                    Paragraph circulationText = new Paragraph(compactFormatter.format(circulationSupplyValue.doubleValue()));
                    Paragraph percentageText = new Paragraph(String.format("(%d%%)", percentageUseOfCirculationSupply));
                    percentageText.getStyle().set("color", "blue");
                    return new HorizontalLayout(circulationText, percentageText);
                })
                .addComponent(bar)
                .build();

        Div circulationSupply = new PortfolioStatsDisplay("Circulation Supply", circulationSupplyContainer);

        Div totalSupply = new PortfolioStatsDisplay("Total Supply", compactFormatter.format(assetTotalSupply.doubleValue()));
        Div volume24Hour = new PortfolioStatsDisplay("Volume 24h", compactFormatter.format(asset24HourVolume));

        Div body = new Div(marketCap, circulationSupply, totalSupply, volume24Hour);
        body.addClassNames("section-card-wrapper", "market-stats-section");

        section.add(title, body);
        return section;
    }

    private Section aboutSection() {
        H3 title = new H3("About " + asset.getFullName());
        title.setClassName("section-title");
        Paragraph description = new Paragraph(instrumentsFacadeService.getAssetDescriptionSummary(asset));
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
        PriceWatchlistComponent watchlistComponent = new PriceWatchlistComponent(asset, actionType, instrumentsFacadeService);

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
        transactionsGrid.setItems(instrumentsFacadeService.getTransactionsByAsset(asset));
        transactionsGrid.setPageSize(10);
        transactionsGrid.addUpdateItemListener(l -> rebuildPage());

        H3 title = new H3("Transactions");
        title.setClassName("section-title");

        Button addTransactionBtn = new Button("Add Transaction", LumoIcon.PLUS.create());
        addTransactionBtn.setIconAfterText(false);
        addTransactionBtn.addClickListener(e -> {
            addTransactionDialog.open();
            transactionsGrid.setItems(instrumentsFacadeService.getTransactionsByAsset(asset));
        });
        Button seeAllTransactionsBtn = new Button("See all transactions");
        Container buttonsContainer = new Container("header-buttons", addTransactionBtn, seeAllTransactionsBtn);

        Container header = new Container("section-header", title, buttonsContainer);

        return new Section(header, transactionsGrid);
    }

    private Container getAssetDiversityContainer(int assetDiversityPercentage) {
        return Container.builder("portfolio-diversity")
                .addComponent(() -> {
                    NumericValueParagraph valueParagraph = new NumericValueParagraph(assetDiversityPercentage, percentageFormatter);
                    valueParagraph.getStyle().setColor("blue");
                    return valueParagraph;
                })
                .addComponent(new ProgressBar(0, 100, assetDiversityPercentage))
                .build();
    }


}