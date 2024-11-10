package com.example.application.views.pages.crypto;

import com.example.application.entities.crypto.Asset;
import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.services.crypto.PortfolioPerformanceTracker;
import com.example.application.utils.common.MathUtils;
import com.example.application.utils.common.StringUtils;
import com.example.application.utils.common.number.CompactFormatter;
import com.example.application.utils.common.number.CurrencyFormatter;
import com.example.application.utils.common.number.PercentageFormatter;
import com.example.application.views.components.PriceMonitorContainer;
import com.example.application.views.components.PriceWatchlistComponent;
import com.example.application.views.components.TransactionsGrid;
import com.example.application.views.components.complex_components.ProfitValueParagraph;
import com.example.application.views.components.complex_components.dialogs.transactions.AddTransactionDialog;
import com.example.application.views.components.complex_components.fields.PricePercentageWrapper;
import com.example.application.views.components.fields.CurrencyField;
import com.example.application.views.components.fields.PercentageField;
import com.example.application.views.components.native_components.Container;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.Component;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigInteger;
import java.util.Objects;

/*
 * TODO:
 *  [!] Make watchers to be sorted on ADDING a new one
 * */

@PermitAll
@PageTitle("Asset Details")
@Route(value = "details", layout = MainLayout.class)
public class AssetDetailsView extends Main implements HasUrlParameter<String> {

    private Asset asset;
    private AddTransactionDialog addTransactionDialog;

    @Autowired
    private InstrumentsFacadeService instrumentsFacadeService;

    @Autowired
    private PortfolioPerformanceTracker portfolioPerformanceTracker;

    private static final CurrencyFormatter currencyFormatter = CurrencyFormatter.withDefaults();
    private static final PercentageFormatter percentageFormatter = PercentageFormatter.withDefaults();

    @Override
    public void setParameter(BeforeEvent beforeEvent, String symbol) {
        this.asset = Objects.requireNonNull(instrumentsFacadeService.getAssetBySymbol(symbol));
        this.addTransactionDialog = new AddTransactionDialog(asset, instrumentsFacadeService);

        buildPage();
        getElement().executeJs("window.scrollTo(0,0)"); // Scroll to top of the page, on initialization
    }

    private void buildPage() {
        setClassName("coin-details-content");

        add(
                headerDetailsSection(),
                holdingsSection(),
                notesAndConvertorSection(),
                createWatchlistSection(AssetWatcher.ActionType.BUY),
                createWatchlistSection(AssetWatcher.ActionType.SELL),
                priceMonitorSection(),
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
                .addComponent(new H1(instrumentsFacadeService.getAssetFullName(asset)))
                .addComponent(() -> {
                    Span dot = new Span("•");
                    dot.setClassName("dot");
                    return dot;
                })
                .addComponent(new Span(asset.getSymbol()))
                .build();

        double price = instrumentsFacadeService.getAssetPrice(asset);
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

    private Section priceMonitorSection() {
        Section section = new Section();
        section.addClassName("section-card-wrapper");
        section.add(new PriceMonitorContainer());

        HorizontalLayout layout = new HorizontalLayout();
        PercentageField percentageField = new PercentageField("Percentage");
        Span estimatedSign = new Span("~");
        Paragraph estimatedMoneyAmount = new Paragraph("$300");
        layout.add(percentageField, estimatedSign, estimatedMoneyAmount);

        section.add(layout);
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

        CurrencyField tokenAmountField = new CurrencyField();
        tokenAmountField.setValue(1);

        CurrencyField usdAmountField = new CurrencyField();
        usdAmountField.setValue(instrumentsFacadeService.getAssetPrice(asset));

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
            double calculatedPrice = tokenAmountField.doubleValue() * instrumentsFacadeService.getAssetPrice(asset);
            usdAmountField.setValue(calculatedPrice);
        });

        usdAmountField.setValueChangeMode(ValueChangeMode.EAGER);
        usdAmountField.addKeyUpListener(e -> {
            double calculatedPrice = usdAmountField.doubleValue() / instrumentsFacadeService.getAssetPrice(asset);
            tokenAmountField.setValue(calculatedPrice);
        });

        Section section = new Section(title, sectionBody);
        section.addClassName("convertor-section");
        return section;
    }

    // https://coinstats.app/coins/usd-coin/holdings/
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

        double assetCost = portfolioPerformanceTracker.getAssetTotalCost(asset);
        double assetWorth = portfolioPerformanceTracker.getAssetTotalWorth(asset);
        double assetProfitLoss = portfolioPerformanceTracker.getAssetProfit(asset);
        double profitLossPercentage = portfolioPerformanceTracker.getAssetProfitPercentage(asset);
        int assetDiversityPercentage = portfolioPerformanceTracker.getAssetDiversityPercentage(asset);

        ProfitValueParagraph costValue = new ProfitValueParagraph(assetCost, currencyFormatter);
        ProfitValueParagraph worthValue = new ProfitValueParagraph(assetWorth, currencyFormatter);
        PricePercentageWrapper profitLossContainer = new PricePercentageWrapper(assetProfitLoss, profitLossPercentage);
        profitLossContainer.setPercentageBadgeBackground(false);

        Container diversityContainer = Container.builder("portfolio-diversity")
                .addComponent(() -> {
                    ProfitValueParagraph valueParagraph = new ProfitValueParagraph(assetDiversityPercentage, percentageFormatter);
                    valueParagraph.getStyle().setColor("blue");
                    return valueParagraph;
                })
                .addComponent(new ProgressBar(0, 100, assetDiversityPercentage))
                .build();

        Div totalCost = createStatsItem("Total Cost", costValue);
        Div totalWorth = createStatsItem("Total Worth", worthValue);
        Div profitLoss = createStatsItem("Profit Loss", profitLossContainer);
        Div diversity = createStatsItem("Portfolio Diversity", diversityContainer);

        Div body = new Div();
        body.addClassName("section-card-wrapper");
        body.add(totalWorth, profitLoss, totalCost, diversity);

        return new Section(header, body);
    }

    private Section marketStatsSection() {
        Section section = new Section();
        H3 title = new H3("Market Stats");
        title.setClassName("section-title");
        CompactFormatter compactFormatter = new CompactFormatter();

        double assetTotalMarketCap = instrumentsFacadeService.getAssetTotalMarketCap(asset);
        Div marketCap = createStatsItem("Market Cap", compactFormatter.format(assetTotalMarketCap));

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

        Div circulationSupply = createStatsItem("Circulation Supply", circulationSupplyContainer);

        Div totalSupply = createStatsItem("Total Supply", compactFormatter.format(assetTotalSupply.doubleValue()));
        Div volume24Hour = createStatsItem("Volume 24h", compactFormatter.format(asset24HourVolume));

        Div body = new Div(marketCap, circulationSupply, totalSupply, volume24Hour);
        body.addClassNames("section-card-wrapper", "market-stats-section");

        section.add(title, body);
        return section;
    }

    private Section aboutSection() {
        H3 title = new H3("About " + instrumentsFacadeService.getAssetFullName(asset));
        title.setClassName("section-title");
        Paragraph description = new Paragraph(instrumentsFacadeService.getAssetDescriptionSummary(asset));
        Container body = new Container("section-card-wrapper", description);
        return new Section(title, body);
    }

    private Div createStatsItem(String labelText, String valueText) {
        Paragraph paragraph = new Paragraph(valueText);
        paragraph.setId(labelText);
        paragraph.addClassName("stats-item");

        NativeLabel label = new NativeLabel(labelText);
        label.setFor(paragraph);
        label.addClassName("stats-title");

        Div div = new Div(label, paragraph);
        div.addClassName("stats-details-wrapper");
        return div;
    }

    private Div createStatsItem(String labelText, Component valueComponent) {
        Paragraph label = new Paragraph(labelText);
        label.addClassName("stats-title");
        Div div = new Div(label, valueComponent);
        valueComponent.addClassName("stats-item");
        div.addClassName("stats-details-wrapper");
        return div;
    }

    private Icon getStarIcon(boolean isMarkedAsFavorite) {
        return isMarkedAsFavorite
                ? VaadinIcon.STAR.create()
                : VaadinIcon.STAR_O.create();
    }

    private Section createWatchlistSection(AssetWatcher.ActionType actionType) {
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
                    addWatchlistBtn.addClickListener(e -> watchlistComponent.addNewPriceLayout());
                    return addWatchlistBtn;
                })
                .build();

        Container body = Container.builder("section-card-wrapper")
                .addComponent(watchlistComponent)
                .build();

        return new Section(header, body);
    }

    private Section transactionHistorySection() {
        TransactionsGrid transactionsGrid = new TransactionsGrid(instrumentsFacadeService);
        transactionsGrid.setItems(instrumentsFacadeService.getTransactionsByAsset(asset));
        transactionsGrid.setPageSize(10);

        Container header = Container.builder("section-header")
                .addComponent(() -> {
                    H3 title = new H3("Transactions");
                    title.setClassName("section-title");
                    return title;
                })
                .addComponent(() -> {
                    Button addTransactionBtn = new Button("Add Transaction", LumoIcon.PLUS.create());
                    addTransactionBtn.setIconAfterText(false);
                    addTransactionBtn.addClickListener(e -> {
                        addTransactionDialog.open();
                        transactionsGrid.setItems(instrumentsFacadeService.getTransactionsByAsset(asset));
                    });
                    return addTransactionBtn;
                })
                .build();

        Button seeAllTransactionsBtn = new Button("See more transactions");

        return new Section(header, transactionsGrid, seeAllTransactionsBtn);
    }

}

