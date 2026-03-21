package com.example.application.data.requests.asset_watchers;

import com.example.application.data.dtos.AssetWatcherDTO;
import com.example.application.entities.common.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public final class UpdateAssetWatcherRequest {

	private final Long id;
	private String assetSymbol;
	private BigDecimal targetPrice;
	private BigDecimal targetAmount;
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
