package com.example.application.asset_watcher;

import com.example.application.transaction.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public final class CreateAssetWatcherRequest {

	@NotNull
	private Long portfolioId;

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

}
