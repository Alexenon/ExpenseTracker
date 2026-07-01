package com.example.application.data.requests.asset_watchers;

import com.example.application.data.dtos.AssetWatcherDTO;
import com.example.application.entities.common.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public final class UpdateAssetWatcherRequest {

	@NotNull
	@Min(value = 1, message = "Invalid id")
	private final Long id;

	@NotBlank
	private String assetSymbol;

	@NotNull
	private TransactionType transactionType;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
	private BigDecimal targetPrice;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
	private BigDecimal targetAmount;

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
