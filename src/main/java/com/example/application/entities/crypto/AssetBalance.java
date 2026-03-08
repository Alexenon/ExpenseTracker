package com.example.application.entities.crypto;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

	@Column(name = "amount", nullable = false)
	private double amount = 0.0;

	@Column(name = "avg_buy_price", nullable = false)
	private double avgBuyPrice = 0.0;

	@Column(name = "avg_sell_price", nullable = false)
	private double avgSellPrice = 0.0;

	@Column(name = "cost", nullable = false)
	private double cost = 0.0;

	@Column(name = "realized", nullable = false)
	private double totalRealized = 0.0;

	@Column(name = "holding_days", nullable = false)
	private double holdingDays = 0.0;

	@Column(name = "last_time_updated", nullable = false)
	private LocalDateTime lastTimeUpdated = LocalDateTime.now();

	@Column(name = "time_created_at", nullable = false, updatable = false)
	private final LocalDateTime timeCreatedAt = LocalDateTime.now();

}