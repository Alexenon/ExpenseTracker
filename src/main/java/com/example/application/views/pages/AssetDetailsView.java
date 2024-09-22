package com.example.application.views.pages;

import com.example.application.data.models.NumberType;
import com.example.application.data.models.crypto.AssetData;
import com.example.application.entities.crypto.AssetWatcher;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.utils.common.MathUtils;
import com.example.application.utils.common.StringUtils;
import com.example.application.views.components.*;
import com.example.application.views.components.complex_components.PriceBadge;
import com.example.application.views.components.complex_components.dialogs.transactions.AddTransactionDialog;
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

/*
 * TODO:
 *  - cursor: not-allowed;    - style something
 * */

@PermitAll
@PageTitle("Asset Details")
@Route(value = "details", layout = MainLayout.class)
public class AssetDetailsView extends Main implements HasUrlParameter<String> {

    private AssetData assetData;
    private AddTransactionDialog addTransactionDialog;

    @Autowired
    private InstrumentsFacadeService instrumentsFacadeService;

    @Override
    public void setParameter(BeforeEvent beforeEvent, String symbol) {
        this.assetData = instrumentsProvider.getListOfAssetData().stream()
                .filter(a -> a.getAsset().getSymbol().equalsIgnoreCase(symbol))
                .findFirst()
                .orElseThrow();

        this.addTransactionDialog = new AddTransactionDialog(assetData, instrumentsFacadeService, instrumentsProvider);

        buildPage();
        getElement().executeJs("window.scrollTo(0,0)"); // Scroll to top of the page, on initialization
    }

    private void buildPage() {
        setClassName("coin-details-content");

        add(
                headerDetailsSection(),
                notesAndConvertorSection(),
                holdingsSection(),
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
                    Image image = new Image(assetData.getAssetMetadata().getLogoUrl(), assetData.getName());
                    image.setClassName("coin-overview-image");
                    return image;
                })
                .addComponent(new H1(assetData.getName()))
                .addComponent(() -> {
                    Span dot = new Span("•");
                    dot.setClassName("dot");
                    return dot;
                })
                .addComponent(new Span(assetData.getAssetMetadata().getSymbol()))
                .build();

        Container priceWrapper = Container.builder()
                .addClassName("price-wrapper")
                .addComponent(() -> {
                    String assetPrice = NumberType.CURRENCY.parse(assetData.getAssetMetadata().getPriceUsd());
                    return new Paragraph(assetPrice);
                })
                .addComponent(() -> {
                    double percentageChangeLast24H = assetData.getAssetMetadata().getSpotMoving24HourChangePercentageUsd();
                    return new PriceBadge(percentageChangeLast24H, NumberType.PERCENT);
                })
                .build();

        Div coinInfoContainer = new Div(rank, coinNameContainer, priceWrapper);

        Button markAsFavorite = new Button(getStarIcon(assetData.getAsset().isMarkedAsFavorite()));
        markAsFavorite.addClassName("rounded-button");
        markAsFavorite.addClickListener(e -> {
            boolean isFavorite = assetData.getAsset().isMarkedAsFavorite();
            assetData.getAsset().setMarkedAsFavorite(!isFavorite);
            markAsFavorite.setIcon(getStarIcon(!isFavorite));
        });

        section.addClassName("asset-details-header");
        section.add(coinInfoContainer, markAsFavorite);

        return section;
    }

    private Section investedDetailsSection() {
        Section section = new Section();
        H3 title = new H3("Portfolio Statistics");
        title.setClassName("section-title");

        Div body = new Div();
        body.addClassNames("section-card-wrapper");


        Div totalInvestedParagraph = createStatsItem("Total Invested in BTC", "$50000");
        Div dollarProfitParagraph = createStatsItem("Total Invested in BTC", "$50000");
        Div percentageProfitParagraph = createStatsItem("", "");


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

        Image inputImage = new Image(assetData.getAssetMetadata().getLogoUrl(), assetData.getName());
        inputImage.setClassName("coin-overview-image");

        CurrencyField tokenAmountField = new CurrencyField();
        tokenAmountField.setValue(1);

        CurrencyField usdAmountField = new CurrencyField();
        usdAmountField.setValue(assetData.getAssetMetadata().getPriceUsd());

        Container inputContainer = Container.builder()
                .addComponent(inputImage)
                .addComponent(new Paragraph(assetData.getAsset().getSymbol()))
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

        // TODO: Deleting all from input looks strange
        // TODO: setValue() should be same formatter as default
        tokenAmountField.setValueChangeMode(ValueChangeMode.EAGER);
        tokenAmountField.addKeyUpListener(e -> {
            double calculatedPrice = tokenAmountField.doubleValue() * assetData.getAssetMetadata().getPriceUsd();
            usdAmountField.setValue(calculatedPrice);
            System.out.println("usdAmountField = " + calculatedPrice);
        });

        usdAmountField.setValueChangeMode(ValueChangeMode.EAGER);
        usdAmountField.addKeyUpListener(e -> {
            double calculatedPrice = usdAmountField.doubleValue() / assetData.getAssetMetadata().getPriceUsd();
            tokenAmountField.setValue(calculatedPrice);
            System.out.println("tokenAmountField = " + calculatedPrice);
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

        Div body = new Div();
        body.addClassName("section-card-wrapper");

        // TODO: Add actual values
        PriceBadge diversityValue = new PriceBadge(8, NumberType.PERCENT, true, false, false);
        ProgressBar diversityBar = new ProgressBar(0, 100, 100.0 / 8);
        Container diversityContainer = Container.builder("portfolio-diversity")
                .addComponent(diversityValue)
                .addComponent(diversityBar)
                .build();

        PriceBadge worthValue = new PriceBadge(200, NumberType.CURRENCY, false, false, false);
        PriceBadge costValue = new PriceBadge(180, NumberType.CURRENCY, false, false, false);
        PriceBadge profitLossValue = new PriceBadge(20, NumberType.CURRENCY, true, false, false);

        Div diversity = createStatsItem("Portfolio Diversity", diversityContainer);
        Div totalWorth = createStatsItem("Total Worth", worthValue);
        Div totalCost = createStatsItem("Total Cost", costValue);
        Div profitLoss = createStatsItem("Profit Loss", profitLossValue);

        body.add(diversity, totalWorth, totalCost, profitLoss);

        return new Section(header, body);
    }

    private Section marketStatsSection() {
        Section section = new Section();
        H3 title = new H3("Market Stats");
        title.setClassName("section-title");

        Div marketCap = createStatsItem("Market Cap",
                MathUtils.formatBigNumber(assetData.getAssetMetadata().getTotalMktCapUsd()));

        BigInteger totalSupplyValue = assetData.getAssetMetadata().getSupplyTotal();
        BigInteger circulationSupplyValue = assetData.getAssetMetadata().getSupplyCirculating();
        int percentageUseOfCirculationSupply = MathUtils.percentageOf(circulationSupplyValue, totalSupplyValue);
        ProgressBar bar = new ProgressBar(0, 100, percentageUseOfCirculationSupply);

        Container circulationSupplyContainer = Container.builder("portfolio-diversity")
                .addComponent(() -> {
                    Paragraph circulationText = new Paragraph(MathUtils.formatBigNumber(circulationSupplyValue));
                    Paragraph percentageText = new Paragraph(String.format("(%d%%)", percentageUseOfCirculationSupply));
                    percentageText.getStyle().set("color", "blue");
                    return new HorizontalLayout(circulationText, percentageText);
                })
                .addComponent(bar)
                .build();

        Div circulationSupply = createStatsItem("Circulation Supply", circulationSupplyContainer);

        Div totalSupply = createStatsItem("Total Supply",
                MathUtils.formatBigNumber(assetData.getAssetMetadata().getSupplyTotal()));
        Div volume24Hour = createStatsItem("Volume 24h",
                MathUtils.formatBigNumber(assetData.getAssetMetadata().getSpotMoving24HourQuoteVolumeUsd()));

        Div body = new Div(marketCap, circulationSupply, totalSupply, volume24Hour);
        body.addClassNames("section-card-wrapper", "market-stats-section");

        section.add(title, body);
        return section;
    }

    private Section aboutSection() {
        H3 title = new H3("About " + assetData.getName());
        title.setClassName("section-title");
        Paragraph description = new Paragraph(assetData.getAssetMetadata().getAssetDescriptionSummary());
        Container body = new Container("section-card-wrapper", description);
        return new Section(title, body);
    }

    private Div createStatsItem(String labelText, String valueText) {
        Paragraph paragraph = new Paragraph(valueText);
        paragraph.setId(labelText);
        paragraph.addClassName("stats-item");

        Label label = new Label(labelText);
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

    private Section transactionHistorySection() {
        Container header = Container.builder("section-header")
                .addComponent(() -> {
                    H3 title = new H3("Transactions");
                    title.setClassName("section-title");
                    return title;
                })
                .addComponent(() -> {
                    Button addHoldingBtn = new Button("Add Transaction", LumoIcon.PLUS.create());
                    addHoldingBtn.setIconAfterText(false);
                    addHoldingBtn.addClickListener(e -> addTransactionDialog.open());
                    return addHoldingBtn;
                })
                .build();

        Button seeAllTransactionsBtn = new Button("See more transactions");

        TransactionsGrid transactionsGrid = new TransactionsGrid(instrumentsFacadeService, instrumentsProvider);
        transactionsGrid.setItems(instrumentsFacadeService.getTransactionsByAsset(assetData.getAsset()));
        transactionsGrid.setPageSize(10);
        return new Section(header, transactionsGrid);
    }

    private Section createWatchlistSection(AssetWatcher.ActionType actionType) {
        PriceWatchlistComponent watchlistComponent = new PriceWatchlistComponent(assetData.getAsset(),
                actionType, instrumentsFacadeService);

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

}

