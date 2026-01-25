package com.example.application.entities.crypto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	@Column(nullable = false, unique = true)
	private String symbol;

	@NotNull
	@Size(min = 1, max = 55)
	@Column(nullable = false)
	private String fullName;

	@Column(name = "market_price", nullable = false)
	private double marketPrice;

	@Column(name = "change_percentage", nullable = false)
	private double changePercentage;

	@Column(name = "summary_description", nullable = false, length = 1000)
	private String summaryDescription = "";

	@Column(name = "total_market_cap", nullable = false)
	private BigInteger totalMarketCap;

	@Column(name = "total_supply", nullable = false)
	private BigInteger totalSupply;

	@Column(name = "circulation_supply", nullable = false)
	private BigInteger circulationSupply;

	@Column(name = "today_volume", nullable = false)
	private double todayVolume;

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

