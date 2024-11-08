package com.example.application.views.pages.crypto.comparator.tabs;

import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.example.application.views.components.native_components.Container;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.data.renderer.LitRenderer;

public abstract class BaseCalculatorTab extends Tab {

    protected final InstrumentsFacadeService instrumentsFacadeService;

    protected Div inputFieldsContainer;
    protected Button displayResultsBtn;
    protected Div resultsContainer = new Container("result-items");

    public BaseCalculatorTab(String label, InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;

        addClassName("compare-tab-content");

        addAttachListener(e -> {
            this.inputFieldsContainer = createInputFieldsContainer();
            this.displayResultsBtn = createDisplayResultsBtn();
            add(
                    new H3(label),
                    inputFieldsContainer,
                    displayResultsBtn,
                    new Container("results", new H3("Results"), resultsContainer)
            );

            inputFieldsContainer.addClassName("comparation-body");
        });
    }

    protected abstract Div createInputFieldsContainer();

    protected abstract Button createDisplayResultsBtn();

    protected Div createResultItem(String labelText, double value) {
        return createResultItem(labelText, String.format("$%.2f", value));
    }

    protected Div createResultItem(String labelText, String valueText) {
        return createResultItem(labelText, new Span(valueText));
    }

    protected Div createResultItem(String labelText, Component content) {
        Div itemContainer = new Div();
        itemContainer.addClassName("result-item");
        itemContainer.add(new Paragraph(labelText), content);
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

    protected double getAssetMarketPrice(ComboBox<Asset> assetSymbolField) {
        Asset selectedAsset = assetSymbolField.getValue();
        if (selectedAsset == null) {
            return 0;
        } else {
            return instrumentsFacadeService.getAssetPrice(selectedAsset);
        }
    }

    protected double getAmountOfTokens(Asset selectedAsset) {
        if (selectedAsset == null) {
            return 0.0;
        } else {
            return instrumentsFacadeService.getAmountOfTokens(selectedAsset);
        }
    }

}
