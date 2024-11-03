package com.example.application.views.pages.crypto.comparator.tabs;

import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.native_components.Container;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.renderer.LitRenderer;

public abstract class BaseCompareTab extends Tab {

    protected final InstrumentsFacadeService instrumentsFacadeService;

    protected Div inputFieldsContainer;
    protected Button displayResultsBtn;
    protected final Container resultsContainer = new Container("result-items");

    protected double assetMarketPrice;

    public BaseCompareTab(String label, InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;

        addClassName("compare-tab-content");
        add(
                new H3(label),
                new Container("results", new H3("Results"), resultsContainer)
        );

        addAttachListener(e -> {
            this.displayResultsBtn = createDisplayResultsBtn();
            this.inputFieldsContainer = createInputFieldsContainer();
            addComponentAtIndex(1, inputFieldsContainer);
            addComponentAtIndex(2, displayResultsBtn);
        });
    }

    protected abstract Div createInputFieldsContainer();

    protected abstract Button createDisplayResultsBtn();


    protected Div createOutputItem(String labelText, double value) {
        return createOutputItem(labelText, String.format("$%.2f", value));
    }

    protected Div createOutputItem(String labelText, String valueText) {
        Div itemContainer = new Div();
        itemContainer.addClassName("result-item");
        itemContainer.add(new Paragraph(labelText), new Span(valueText));
        return itemContainer;
    }

    protected LitRenderer<Asset> assetSymbolRenderer() {
        return LitRenderer.<Asset>of(
                        "<div class='coin-overview-name-container'>" +
                                "  <img class='rounded coin-overview-image' src='${item.imgUrl}' alt='${item.fullName}'/>" +
                                "  <span>${item.symbol}</span>" +
                                "  <p>${item.fullName}</p>" +
                                "</div>")
                .withProperty("imgUrl", instrumentsFacadeService::getAssetImgUrl)
                .withProperty("symbol", Asset::getSymbol)
                .withProperty("fullName", instrumentsFacadeService::getAssetFullName);
    }

    public String getSelectedAssetSymbol(ComboBox<Asset> assetSymbolField) {
        Asset selectedAsset = assetSymbolField.getValue();
        return selectedAsset == null ? "" : selectedAsset.getSymbol();
    }

    protected void updateAssetMarketPrice(ComboBox<Asset> assetSymbolField) {
        Asset selectedAsset = assetSymbolField.getValue();
        if (selectedAsset == null) {
            assetMarketPrice = 0;
        } else {
            assetMarketPrice = instrumentsFacadeService.getAssetPrice(selectedAsset);
        }
    }

}
