package com.example.application.views.pages;

import com.example.application.data.models.Asset;
import com.example.application.data.models.InstrumentsProvider;
import com.example.application.views.components.PriceMonitorContainer;
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
        Div priceChangeBadge = createPriceChangeBadge(asset.getAssetData().getSpotMoving24HourChangePercentageUsd());
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

    private Section marketStatsSection() {
        Div marketCap = createMarketStatsItem("Market Cap",
                String.valueOf(asset.getAssetData().getTotalMktCapUsd()));
        Div circulationSupply = createMarketStatsItem("Circulation Supply",
                String.valueOf(asset.getAssetData().getSupplyCirculating()));
        Div totalSupply = createMarketStatsItem("Total Supply",
                String.valueOf(asset.getAssetData().getSupplyTotal()));
        Div volume24Hour = createMarketStatsItem("Volume 24h",
                String.valueOf(asset.getAssetData().getSpotMoving24HourQuoteVolumeUsd()));

        Section section = new Section(marketCap, circulationSupply, totalSupply, volume24Hour);
        section.addClassNames("section-card-wrapper", "market-stats-section");
        return section;
    }

    private Section aboutSection() {
        Paragraph description = new Paragraph(asset.getAssetData().getAssetDescriptionSummary());
        return new Section(description);
    }

    private Section holdingsSection() {
        return new Section();
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

    private Div createPriceChangeBadge(double priceChange) {
        String formattedPercentage = String.format("%.2f%%", priceChange);
        Paragraph percentage = new Paragraph(formattedPercentage);

        Icon iconDirection;
        if (priceChange > 0) {
            iconDirection = VaadinIcon.ARROW_UP.create();
            iconDirection.setClassName("price-text-green");
            percentage.setClassName("price-badge-green");
        } else if (priceChange < 0) {
            iconDirection = VaadinIcon.ARROW_DOWN.create();
            iconDirection.setClassName("price-text-red");
            percentage.setClassName("price-badge-red");
        } else {
            iconDirection = VaadinIcon.MINUS.create();
            iconDirection.removeClassNames("price-text-green", "price-text-red");
            percentage.removeClassNames("price-badge-green", "price-badge-red");
        }

        Div priceWrapper = new Div(iconDirection, percentage);
        priceWrapper.setClassName("price-change-badge");
        return priceWrapper;
    }


    /*
        Take a look at the DecimalFormat class. Most people use it for formatting numbers as strings,
        but it actually has a parse method to go the other way around! You initialize it with your
        pattern (see the tutorial), and then invoke parse() on the input string.
    */

}

