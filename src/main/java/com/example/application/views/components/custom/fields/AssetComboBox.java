package com.example.application.views.components.custom.fields;

import com.example.application.data.dtos.AssetDTO;
import com.example.application.services.crypto.InstrumentsFacadeService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.renderer.LitRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Function;

@Component
public class AssetComboBox extends ComboBox<AssetDTO> {

	private final InstrumentsFacadeService instrumentsFacadeService;

	@Autowired
	public AssetComboBox(InstrumentsFacadeService instrumentsFacadeService) {
		this.instrumentsFacadeService = instrumentsFacadeService;
		initialize();
	}

	private void initialize() {
		setLabel("Asset");
		setItemsWithFilter();
		setItemLabelGenerator(AssetDTO::getFullName);
		setRenderer(assetSymbolRenderer());
	}

	public void setValue(String symbol) {
		AssetDTO asset = Optional.ofNullable(symbol)
				.flatMap(a -> instrumentsFacadeService.getAssetBySymbol(symbol))
				.orElse(null);

		setValue(asset);
	}

	private void setItemsWithFilter() {
		ComboBox.ItemFilter<AssetDTO> defaultFilter = (asset, filterString) -> {
			String lowercaseInput = filterString.toLowerCase();
			String lowercaseSymbol = asset.getSymbol().toLowerCase();
			String lowercaseName = asset.getFullName().toLowerCase();

			return lowercaseSymbol.startsWith(lowercaseInput) || lowercaseName.startsWith(lowercaseInput);
		};

		this.setItems(defaultFilter, instrumentsFacadeService.getAllAssets());
	}

	protected LitRenderer<AssetDTO> assetSymbolRenderer() {
		String templateExpression = """
				<div class='coin-overview-name-container'>
				  <img class='rounded coin-overview-image' src='${item.imgUrl}' alt='${item.fullName}'/>
				  <span>${item.symbol}</span>
				  <p>${item.fullName}</p>
				</div>""";
		return LitRenderer.<AssetDTO>of(templateExpression)
				.withProperty("imgUrl", AssetDTO::getImageUrl)
				.withProperty("symbol", AssetDTO::getSymbol)
				.withProperty("fullName", AssetDTO::getFullName);
	}

	public AssetDTO getSelectedAsset() {
		return this.getValue();
	}

	public String getSymbol() {
		return extract(AssetDTO::getSymbol, "");
	}

	public BigDecimal getMarketPrice() {
		return extract(AssetDTO::getMarketPrice, BigDecimal.ZERO);
	}

	public BigDecimal getAmountTokens(Long portfolioId) {
		return extract(asset -> instrumentsFacadeService.getAmountOfTokens(portfolioId, asset.getSymbol()), BigDecimal.ZERO);
	}

	public <R> R extract(Function<AssetDTO, R> getter, R defaultValue) {
		return Optional.ofNullable(this.getValue())
				.map(getter)
				.orElse(defaultValue);
	}

}