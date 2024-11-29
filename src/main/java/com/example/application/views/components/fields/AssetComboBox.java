package com.example.application.views.components.fields;

import com.example.application.entities.crypto.Asset;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.renderer.LitRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
public class AssetComboBox extends ComboBox<Asset> {

    private final InstrumentsFacadeService instrumentsFacadeService;

    @Autowired
    public AssetComboBox(InstrumentsFacadeService instrumentsFacadeService) {
        this.instrumentsFacadeService = instrumentsFacadeService;
        initialize();
    }

    private void initialize() {
        setLabel("Asset");
        setItemsWithFilter();
        setItemLabelGenerator(Asset::getFullName);
        setRenderer(assetSymbolRenderer());
    }

    private void setItemsWithFilter() {
        ComboBox.ItemFilter<Asset> defaultFilter = (asset, filterString) -> {
            String lowercaseInput = filterString.toLowerCase();
            String lowercaseSymbol = asset.getSymbol().toLowerCase();
            String lowercaseName = asset.getFullName().toLowerCase();

            return lowercaseSymbol.startsWith(lowercaseInput) || lowercaseName.startsWith(lowercaseInput);
        };

        this.setItems(defaultFilter, instrumentsFacadeService.getAllAssets());
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
                .withProperty("fullName", Asset::getFullName);
    }

    public Asset getSelectedAsset() {
        return this.getValue();
    }

    public String getSymbol() {
        return extract(Asset::getSymbol, "");
    }

    public double getMarketPrice() {
        return extract(instrumentsFacadeService::getAssetPrice, 0.0);
    }

    public double getAmountTokens() {
        return extract(instrumentsFacadeService::getAmountOfTokens, 0.0);
    }

    public <R> R extract(Function<Asset, R> getter, R defaultValue) {
        return Optional.of(this.getValue())
                .map(getter)
                .orElse(defaultValue);
    }


}
