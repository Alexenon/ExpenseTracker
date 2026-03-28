package com.example.application.entities.crypto;

import com.example.application.entities.common.TransactionType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/*
    TODO [LONG TERM]:
        [!] Add SpotPairs - BTC/USDT, BTC/USDC, ...
        [!] DEPOSIT / TRANSFER - Add asset amount from external sources
        [!] CONVERT  - Switch from one asset to another

*/

@Data
@Entity(name = "transactions")
@EqualsAndHashCode(of = {"id", "portfolio", "asset"})
@NoArgsConstructor
public class Transaction {

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
	@Column(name = "market_price", nullable = false, precision = 38, scale = 20)
	@DecimalMin(value = "0.0", inclusive = false, message = "Market price must be greater than 0")
	private BigDecimal marketPrice;

	@Column(name = "order_total_cost", nullable = false, precision = 38, scale = 20)
	@DecimalMin(value = "0.0", inclusive = false, message = "Order total cost must be greater than 0")
	private BigDecimal orderTotalCost;

	@Column(name = "order_quantity", nullable = false, precision = 38, scale = 18)
	@DecimalMin(value = "0.0", inclusive = false, message = "Order quantity must be greater than 0")
	private BigDecimal orderQuantity;

	@Column(name = "avg_buy_price_at_moment", nullable = false, precision = 38, scale = 20)
	@DecimalMin(value = "0.0", message = "Average buy price cannot be negative")
	private BigDecimal avgBuyPriceAtMoment;

	@NotNull
	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	private TransactionType type;

	@Nullable
	@Column(name = "note")
	private String note;

	@NotNull
	@Column(name = "last_time_updated", nullable = false)
	private LocalDateTime lastTimeUpdated = LocalDateTime.now();

	@NotNull
	@Column(name = "date_time", nullable = false)
	private LocalDateTime dateTime;

	public Transaction(Transaction transaction) {
		Objects.requireNonNull(transaction, "transaction");
		this.id = transaction.id;
		this.portfolio = transaction.portfolio;
		this.asset = transaction.asset;
		this.marketPrice = transaction.marketPrice;
		this.orderTotalCost = transaction.orderTotalCost;
		this.orderQuantity = transaction.orderQuantity;
		this.avgBuyPriceAtMoment = transaction.avgBuyPriceAtMoment;
		this.type = transaction.type;
		this.note = transaction.note;
		this.lastTimeUpdated = transaction.lastTimeUpdated;
		this.dateTime = transaction.dateTime;
	}

	public boolean isBuyTransaction() {
		return type.isBuyTransaction();
	}

	public boolean isSellTransaction() {
		return type.isSellTransaction();
	}

	@Override
	public String toString() {
		return "Transaction{" +
			   "id=" + id +
			   ", asset=" + asset +
			   ", marketPrice=" + marketPrice +
			   ", orderTotalCost=" + orderTotalCost +
			   ", orderQuantity=" + orderQuantity +
			   ", type=" + type +
			   ", notes='" + note + '\'' +
			   ", dateTime=" + dateTime +
			   '}';
	}
}