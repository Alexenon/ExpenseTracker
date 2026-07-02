package com.example.application.asset;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class CreateAssetRequest {

	@NotBlank
	@Size(min = 1, max = 12)
	private String symbol;

	@NotBlank
	@Size(min = 1, max = 55)
	private String fullName;

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
