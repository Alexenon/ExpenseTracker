package com.example.application.entities.crypto;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.StringJoiner;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "assets")
public class Asset {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(min = 1, max = 12)
	@Column(unique = true, nullable = false)
	private String symbol;

	@NotNull
	@Size(min = 1, max = 55)
	@Column(nullable = false)
	private String fullName;

	@NotNull
	@Column(name = "summary_description", nullable = false, length = 1000)
	private String summaryDescription = "";

	@NotNull
	@Column(name = "market_price", nullable = false, precision = 38, scale = 20)
	@DecimalMin(value = "0.0", inclusive = false, message = "Market price must be greater than 0")
	private BigDecimal marketPrice;

	@NotNull
	@Column(name = "change_percentage", nullable = false)
	private BigDecimal changePercentage;

	@NotNull
	@Column(name = "total_market_cap", nullable = false)
	private BigInteger totalMarketCap;

	@NotNull
	@Column(name = "total_supply", nullable = false)
	private BigInteger totalSupply;

	@NotNull
	@Column(name = "circulation_supply", nullable = false)
	private BigInteger circulationSupply;

	@NotNull
	@Column(name = "today_volume", nullable = false)
	private BigInteger todayVolume;

	@NotNull
	@Column(name = "image_url", nullable = false)
	private String imageUrl = "";

	@Override
	public String toString() {
		return "Asset{id=%d, symbol='%s', fullName='%s'}".formatted(id, symbol, fullName);
	}

	public String toFullString() {
		return new StringJoiner(", ", Asset.class.getSimpleName() + "[", "]")
				.add("id=" + id)
				.add("symbol='" + symbol + "'")
				.add("fullName='" + fullName + "'")
				.add("marketPrice=" + marketPrice)
				.add("changePercentage=" + changePercentage)
				.add("summaryDescription='" + summaryDescription + "'")
				.add("totalMarketCap=" + totalMarketCap)
				.add("totalSupply=" + totalSupply)
				.add("circulationSupply=" + circulationSupply)
				.add("todayVolume=" + todayVolume)
				.add("imageUrl='" + imageUrl + "'")
				.toString();
	}
}
