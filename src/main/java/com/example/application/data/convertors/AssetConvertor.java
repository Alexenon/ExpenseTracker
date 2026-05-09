package com.example.application.data.convertors;

import com.example.application.data.enums.SymbolIndentifier;
import com.example.application.data.requests.asset.CreateAssetRequest;
import com.example.application.data.requests.asset.UpdateAssetRequest;
import com.example.application.entities.crypto.Asset;
import com.example.application.utils.fetchers.crypto_compare.response.AssetMetadata;
import org.springframework.stereotype.Component;

@Component
public class AssetConvertor {

	public static CreateAssetRequest mapToCreateRequest(SymbolIndentifier indentifier, AssetMetadata metadata) {
		CreateAssetRequest request = new CreateAssetRequest();
		request.setSymbol(indentifier.name());
		request.setFullName(indentifier.getFullName());
		request.setSummaryDescription(metadata.getAssetDescriptionSummary());
		request.setMarketPrice(metadata.getPriceUsd());
		request.setChangePercentage(metadata.getSpotMoving24HourChangePercentageUsd());
		request.setTodayVolume(metadata.getSpotMoving24HourQuoteVolumeUsd());
		request.setCirculationSupply(metadata.getSupplyCirculating());
		request.setTotalSupply(metadata.getSupplyTotal());
		request.setTotalMarketCap(metadata.getTotalMktCapUsd());
		request.setImageUrl(metadata.getLogoUrl());
		return request;
	}

	public static UpdateAssetRequest mapToUpdateRequest(Long assetId, AssetMetadata metadata) {
		UpdateAssetRequest request = new UpdateAssetRequest();
		request.setAssetId(assetId);
		request.setSummaryDescription(metadata.getAssetDescriptionSummary());
		request.setMarketPrice(metadata.getPriceUsd());
		request.setChangePercentage(metadata.getSpotMoving24HourChangePercentageUsd());
		request.setTodayVolume(metadata.getSpotMoving24HourQuoteVolumeUsd());
		request.setCirculationSupply(metadata.getSupplyCirculating());
		request.setTotalSupply(metadata.getSupplyTotal());
		request.setTotalMarketCap(metadata.getTotalMktCapUsd());
		request.setImageUrl(metadata.getLogoUrl());
		return request;
	}

	public static Asset mapToEntity(CreateAssetRequest request) {
		Asset asset = new Asset();
		asset.setSymbol(request.getSymbol());
		asset.setFullName(request.getFullName());
		asset.setSummaryDescription(request.getSummaryDescription());
		asset.setMarketPrice(request.getMarketPrice());
		asset.setChangePercentage(request.getChangePercentage());
		asset.setTodayVolume(request.getTodayVolume());
		asset.setCirculationSupply(request.getCirculationSupply());
		asset.setTotalSupply(request.getTotalSupply());
		asset.setTotalMarketCap(request.getTotalMarketCap());
		asset.setImageUrl(request.getImageUrl());
		return asset;
	}

}
