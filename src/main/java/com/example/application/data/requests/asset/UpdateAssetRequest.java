package com.example.application.data.requests.asset;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class UpdateAssetRequest {

	@NotNull
	@Min(value = 1, message = "Invalid id")
	private Long assetId;

	@NotBlank
	@Size(max = 1000)
	private String summaryDescription;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false, message = "Market price must be greater than 0")
	private BigDecimal marketPrice;

	@NotNull
	private BigDecimal changePercentage;

	@NotNull
	@Min(0)
	private BigInteger totalMarketCap;

	@NotNull
	@Min(0)
	private BigInteger totalSupply;

	@NotNull
	@Min(0)
	private BigInteger circulationSupply;

	@NotNull
	@Min(0)
	private BigInteger todayVolume;

	@NotBlank
	private String imageUrl;

}
