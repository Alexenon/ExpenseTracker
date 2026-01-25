package com.example.application.data.requests.asset_watchers;

import com.example.application.data.dtos.AssetWatcherDTO;
import com.example.application.entities.common.TransactionType;
import lombok.Data;

@Data
public final class UpdateAssetWatcherRequest {

	private final Long id;
	private String assetSymbol;
	private double targetPrice;
	private double targetAmount;
	private TransactionType transactionType;
	private boolean isCompleted;

	public UpdateAssetWatcherRequest(AssetWatcherDTO dto) {
		this.id = dto.getId();
		this.assetSymbol = dto.getAssetSymbol();
		this.targetPrice = dto.getTargetPrice();
		this.targetAmount = dto.getTargetAmount();
		this.transactionType = dto.getTransactionType();
		this.isCompleted = dto.isCompleted();
	}
}
