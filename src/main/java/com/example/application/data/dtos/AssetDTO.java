package com.example.application.data.dtos;

import com.example.application.entities.crypto.Asset;
import lombok.Data;

import java.math.BigInteger;

@Data
public class AssetDTO {

	private Long id;
	private String symbol;
	private String fullName;
	private String description;
	private double marketPrice;
	private double changePercentage;
	private BigInteger totalMarketCap;
	private BigInteger totalSupply;
	private BigInteger circulationSupply;
	private double todayVolume;
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
