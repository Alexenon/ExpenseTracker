package com.example.application.asset_watcher;

import com.example.application.transaction.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssetWatcherDTO {

	private final Long id;
	private Long portfolioId;
	private String assetSymbol;
	private BigDecimal targetPrice;
	private BigDecimal targetAmount;
	private TransactionType transactionType;
	private boolean isCompleted;

	public AssetWatcherDTO() {
		this.id = null;
	}

	public AssetWatcherDTO(AssetWatcher watcher) {
		this.id = watcher.getId();
		this.portfolioId = watcher.getPortfolio().getId();
		this.assetSymbol = watcher.getAsset().getSymbol();
		this.targetPrice = watcher.getTargetPrice();
		this.targetAmount = watcher.getTargetAmount();
		this.transactionType = watcher.getTransactionType();
		this.isCompleted = watcher.isCompleted();
	}

}
