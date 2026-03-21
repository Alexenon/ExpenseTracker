package com.example.application.entities.crypto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
	TODO: [URGENT] VERY
		- Add for each entity annotations like @Size, @Min, @Max, @NotBlank...
* */

@Data
@Entity(name = "asset_balances")
@EqualsAndHashCode(of = {"id", "portfolio", "asset"})
@NoArgsConstructor
public class AssetBalance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "portfolio_id", nullable = false)
	private Portfolio portfolio;

	@ManyToOne
	@JoinColumn(name = "asset_id", nullable = false)
	private Asset asset;

	@Column(name = "amount", nullable = false, precision = 38, scale = 18)
	private BigDecimal amount = BigDecimal.ZERO;

	@Column(name = "avg_buy_price", nullable = false, precision = 38, scale = 20)
	private BigDecimal avgBuyPrice = BigDecimal.ZERO;

	@Column(name = "avg_sell_price", nullable = false, precision = 38, scale = 20)
	private BigDecimal avgSellPrice = BigDecimal.ZERO;

	/**
	 * Total cost for the amount of tokens that are in the portfolio
	 */
	@Column(name = "cost", nullable = false, precision = 38, scale = 20)
	private BigDecimal cost = BigDecimal.ZERO;

	/**
	 * Total amount of tokens that was bought across all transactions
	 */
	@Column(name = "total_bought_quantity", nullable = false, precision = 38, scale = 18)
	private BigDecimal totalBoughtQuantity = BigDecimal.ZERO;

	/**
	 * Total amount of tokens that was sold across all transactions
	 */
	@Column(name = "total_sold_quantity", nullable = false, precision = 38, scale = 18)
	private BigDecimal totalSoldQuantity = BigDecimal.ZERO;

	/**
	 * Total money spent on all buy transactions.
	 */
	@Column(name = "total_buy_cost", nullable = false, precision = 38, scale = 20)
	private BigDecimal totalBuyCost = BigDecimal.ZERO;

	/**
	 * Total money received from all sells.
	 */
	@Column(name = "total_sell_value", nullable = false, precision = 38, scale = 20)
	private BigDecimal totalSellValue = BigDecimal.ZERO;

	/**
	 * Profit made from tokens that were already sold
	 */
	@Column(name = "total_realized_profit", nullable = false, precision = 38, scale = 20)
	private BigDecimal totalRealizedProfit = BigDecimal.ZERO;

	@Column(name = "holding_days", nullable = false)
	private double holdingDays = 0.0;

	@Column(name = "last_time_updated", nullable = false)
	private LocalDateTime lastTimeUpdated = LocalDateTime.now();

	@Column(name = "time_created_at", nullable = false, updatable = false)
	private final LocalDateTime timeCreatedAt = LocalDateTime.now();

}