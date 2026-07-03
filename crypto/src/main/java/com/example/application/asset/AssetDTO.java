package com.example.application.asset;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class AssetDTO {

	private Long id;
	private String symbol;
	private String fullName;
	private String description;
	private BigDecimal marketPrice;
	private BigDecimal changePercentage;
	private BigInteger totalMarketCap;
	private BigInteger totalSupply;
	private BigInteger circulationSupply;
	private BigInteger todayVolume;
	private String imageUrl;

	public static AssetDTO mappedFrom(Asset asset) {
		AssetDTO dto = new AssetDTO();
		dto.setId(asset.getId());
		dto.setSymbol(asset.getSymbol());
		dto.setFullName(asset.getFullName());
		dto.setDescription(asset.getSummaryDescription());
		dto.setMarketPrice(asset.getMarketPrice());
		dto.setChangePercentage(asset.getChangePercentage());
		dto.setTotalMarketCap(asset.getTotalMarketCap());
		dto.setTotalSupply(asset.getTotalSupply());
		dto.setCirculationSupply(asset.getCirculationSupply());
		dto.setTodayVolume(asset.getTodayVolume());
		dto.setImageUrl(asset.getImageUrl());
		return dto;
	}




}
