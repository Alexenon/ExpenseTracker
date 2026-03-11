package com.example.application.entities.crypto;

import jakarta.persistence.*;
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

	public static final double MISSING_DOUBLE_VALUE = Double.NaN;
	public static final BigInteger MISSING_BIG_INTEGER_VALUE = BigInteger.valueOf(-1);
	public static final BigDecimal MISSING_BIG_DECIMAL_VALUE = BigDecimal.valueOf(-1);

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String symbol;

	@Column(nullable = false)
	private String fullName;

	@Column(nullable = false)
	private double marketPrice;

	@Column(nullable = false)
	private double changePercentage;

	@Column(nullable = false, length = 1000)
	private String summaryDescription = "";

	@Column(nullable = false)
	private BigInteger totalMarketCap;

	@Column(nullable = false)
	private BigInteger totalSupply;

	@Column(nullable = false)
	private BigInteger circulationSupply;

	@Column(nullable = false)
	private double todayVolume;

	@Column(nullable = false)
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

