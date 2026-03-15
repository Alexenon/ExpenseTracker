package com.example.application.data.dtos;

import com.example.application.entities.common.TransactionType;
import com.example.application.entities.crypto.AssetWatcher;
import lombok.Data;

@Data
public class AssetWatcherDTO {

	private final Long id;
	private Long portfolioId;
	private String assetSymbol;
	private double targetPrice;
	private double targetAmount;
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
