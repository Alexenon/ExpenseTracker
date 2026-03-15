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
	private double totalRealizedProfit = 0.0;
	private double totalBoughtQuantity = 0.0;
	private double totalSoldQuantity = 0.0;
	private double totalBuyCost = 0.0;
	private double totalSellValue = 0.0;
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
		dto.setTotalRealizedProfit(assetBalance.getTotalRealizedProfit());
		dto.setHoldingDays(assetBalance.getHoldingDays());
		dto.setTotalBuyCost(assetBalance.getTotalBuyCost());
		dto.setTotalSellValue(assetBalance.getTotalSellValue());
		dto.setTotalBoughtQuantity(assetBalance.getTotalBoughtQuantity());
		dto.setTotalSoldQuantity(assetBalance.getTotalSoldQuantity());
		return dto;
	}


}
