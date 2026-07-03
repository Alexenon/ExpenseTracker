package com.example.application.asset_watcher;

import com.example.application.asset.Asset;
import com.example.application.portfolio.Portfolio;
import com.example.application.transaction.TransactionType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/*
	TODO: [LONG-TEM]
		- @param targetType      represents the type of the target - price / percentage
* */

/**
 * {@code targetPrice}     represents the price target or percentage target of the asset price<br>
 * {@code targetAmount}    represents the amount to invest when the target is reached<br>
 * {@code transactionType} represents the type of the watcher - buy / sell<br>
 * {@code isCompleted}     represents if the target was achieved<br>
 */
@Data
@Entity(name = "asset_watchers")
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AssetWatcher {

	@Nullable
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "portfolio_id", nullable = false)
	private Portfolio portfolio;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "asset_id", nullable = false)
	private Asset asset;

	@NotNull
	@Column(name = "target_price", nullable = false)
	@DecimalMin(value = "0.0", inclusive = false, message = "Target price value must be greater than 0")
	private BigDecimal targetPrice;

	@NotNull
	@Column(name = "target_amount", nullable = false)
	@DecimalMin(value = "0.0", inclusive = false, message = "Target amount must be greater than 0")
	private BigDecimal targetAmount;

	@NotNull
	@Column(name = "transaction_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;

	@Column(name = "is_completed", nullable = false)
	private boolean isCompleted;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AssetWatcher assetWatcher)) return false;

		return id != null && id.equals(assetWatcher.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

}
