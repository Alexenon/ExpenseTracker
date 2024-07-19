package com.example.application.views.pages;

import com.example.application.data.models.Asset;
import com.example.application.data.models.InstrumentsProvider;
import com.example.application.views.components.PriceMonitorContainer;
import com.example.application.views.components.complex_components.PriceBadge;
import com.example.application.views.layouts.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoIcon;

import java.text.NumberFormat;
import java.util.Locale;

// TODO: Think about Holdings + Transactions

@AnonymousAllowed
@PageTitle("Asset Details")
@Route(value = "details", layout = MainLayout.class)
public class AssetDetailsView extends Main implements HasUrlParameter<String> {

    private final PriceMonitorContainer priceMonitorContainer = new PriceMonitorContainer();
    private final TextArea notesArea = new TextArea();
    private Asset asset;

    @Override
    public void setParameter(BeforeEvent beforeEvent, String assetName) {
        this.asset = InstrumentsProvider.getInstance().getAssets()
                .stream().filter(a -> a.getSymbol().equalsIgnoreCase(assetName))
                .findFirst().orElseThrow();

        buildPage();
        getElement().executeJs("window.scrollTo(0,0)"); // Scroll to top of the page, on initialization
    }

    private void buildPage() {
        setClassName("coin-details-content");

        add(
                headerDetailsSection(),
                priceMonitorContainer,
                notesArea,
                holdingsSection(),
                marketStatsSection(),
                aboutSection()
        );
    }

    private Section headerDetailsSection() {
        Section section = new Section();
        Paragraph rank = new Paragraph("RANK #4");
        rank.setClassName("coin-overview-rank");

        Div coinNameContainer = new Div();
        Image image = new Image(asset.getAssetData().getLogoUrl(), asset.getAssetData().getName());
        image.setClassName("coin-overview-image");
        H1 coinName = new H1(asset.getAssetData().getName());
        Span dot = new Span("•");
        dot.setClassName("dot");
        Span symbol = new Span(asset.getAssetData().getSymbol());
        coinNameContainer.setClassName("coin-overview-name-container");
        coinNameContainer.add(image, coinName, dot, symbol);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        String priceValue = currencyFormat.format(asset.getAssetData().getPriceUsd());
        Paragraph price = new Paragraph(priceValue);
        Div priceChangeBadge = new PriceBadge(asset.getAssetData().getSpotMoving24HourChangePercentageUsd());
        Div priceWrapper = new Div(price, priceChangeBadge);
        priceWrapper.setClassName("price-wrapper");

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

    private Section investedDetailsSection() {
        Section section = new Section();
        Paragraph totalInvestedParagraph = new Paragraph();
        Paragraph dollarProfitParagraph = new Paragraph();
        Paragraph percentageProfitParagraph = new Paragraph();
        return section;
    }

    private Section aboutSection() {
        Paragraph description = new Paragraph(asset.getAssetData().getAssetDescriptionSummary());
        return new Section(description);
    }

    private Section holdingsSection() {
        H3 title = new H3("Holdings");
        title.setClassName("section-title");
        Button addTransactionBtn = new Button("Add Transaction", LumoIcon.PLUS.create());
        addTransactionBtn.setIconAfterText(false);

        Div header = new Div(title, addTransactionBtn);
        header.setClassName("holdings-section-header");

        Div body = new Div();
        body.addClassName("section-card-wrapper");

        Div diversity = createMarketStatsItem("Portfolio Diversity", "8%");
        Div totalWorth = createMarketStatsItem("Total Worth", "$200");
        Div totalCost = createMarketStatsItem("Total Cost", "$180");
        Div profitLoss = createMarketStatsItem("Profit Loss", "$20");

        body.add(diversity, totalWorth, totalCost, profitLoss);

        return new Section(header, body);
    }

    private Section marketStatsSection() {
        Section section = new Section();
        H3 title = new H3("Market Stats");
        title.setClassName("section-title");

        Div marketCap = createMarketStatsItem("Market Cap",
                String.valueOf(asset.getAssetData().getTotalMktCapUsd()));
        Div circulationSupply = createMarketStatsItem("Circulation Supply",
                String.valueOf(asset.getAssetData().getSupplyCirculating()));
        Div totalSupply = createMarketStatsItem("Total Supply",
                String.valueOf(asset.getAssetData().getSupplyTotal()));
        Div volume24Hour = createMarketStatsItem("Volume 24h",
                String.valueOf(asset.getAssetData().getSpotMoving24HourQuoteVolumeUsd()));

        Div body = new Div(marketCap, circulationSupply, totalSupply, volume24Hour);
        body.addClassNames("section-card-wrapper", "market-stats-section");

        section.add(title, body);
        return section;
    }

    private Section transactionSection() {
        return new Section();
    }

    private Div createMarketStatsItem(String labelText, String valueText) {
        Paragraph paragraph = new Paragraph(valueText);
        paragraph.setId(labelText);

        Label label = new Label(labelText);
        label.setFor(paragraph);

        Div div = new Div(label, paragraph);
        div.addClassName("market-stats-item");
        return div;
    }

    private Icon getStarIcon(boolean isMarkedAsFavorite) {
        return isMarkedAsFavorite
                ? VaadinIcon.STAR.create()
                : VaadinIcon.STAR_O.create();
    }



    /*
        Take a look at the DecimalFormat class. Most people use it for formatting numbers as strings,
        but it actually has a parse method to go the other way around! You initialize it with your
        pattern (see the tutorial), and then invoke parse() on the input string.
    */

}

