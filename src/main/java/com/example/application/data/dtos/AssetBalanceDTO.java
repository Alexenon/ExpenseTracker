package com.example.application.data.dtos;

import com.example.application.entities.crypto.AssetBalance;
import lombok.Data;

@Data
public class AssetBalanceDTO {

	private Long id;
	private Long portfolioId;
	private String assetSymbol;
	private double amount = 0.0;
	private double avgBuyPrice = 0.0;
	private double avgSellPrice = 0.0;
	private double cost = 0.0;
	private double totalRealized = 0.0;
	private double holdingDays = 0.0;

	public static AssetBalanceDTO mappedFrom(AssetBalance assetBalance) {
		AssetBalanceDTO dto = new AssetBalanceDTO();
		dto.setId(assetBalance.getId());
		dto.setPortfolioId(assetBalance.getPortfolio().getId());
		dto.setAssetSymbol(assetBalance.getAsset().getSymbol());
		dto.setAmount(assetBalance.getAmount());
		dto.setAvgBuyPrice(assetBalance.getAvgBuyPrice());
		dto.setAvgSellPrice(assetBalance.getAvgSellPrice());
		dto.setCost(assetBalance.getCost());
		dto.setTotalRealized(assetBalance.getTotalRealized());
		dto.setHoldingDays(assetBalance.getHoldingDays());
		return dto;
	}



}
