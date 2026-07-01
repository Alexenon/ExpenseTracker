package com.example.application.data.dtos;

import com.example.application.entities.crypto.AssetBalance;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssetBalanceDTO {

	private Long id;
	private Long portfolioId;
	private String assetSymbol;
	private BigDecimal amount = BigDecimal.ZERO;
	private BigDecimal avgBuyPrice = BigDecimal.ZERO;
	private BigDecimal avgSellPrice = BigDecimal.ZERO;
	private BigDecimal cost = BigDecimal.ZERO;
	private BigDecimal totalRealizedProfit = BigDecimal.ZERO;
	private BigDecimal totalBoughtQuantity = BigDecimal.ZERO;
	private BigDecimal totalSoldQuantity = BigDecimal.ZERO;
	private BigDecimal totalBuyCost = BigDecimal.ZERO;
	private BigDecimal totalSellValue = BigDecimal.ZERO;
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
