package com.example.application.entities.crypto;

import com.example.application.entities.common.TransactionType;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/*
    TODO [LONG TERM]:
        - Add SpotPairs - BTC/USDT, BTC/USDC, ...
        - TRANSFER - Add asset amount from external sources
        - CONVERT  - Switch from one asset to another

    TODO: [NEXT]
        - store avgBuyPrice at the moment
            -> for sell transaction to display then aproximateProfit at the moment
            -> for buy transaction to display then how good was buy compared with previous buy transactions
        - Add profit compared with current price, that should be not stored here but just displayed
*/
@Data
@Entity(name = "transactions")
@NoArgsConstructor
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "portfolio_id", nullable = false)
	private Portfolio portfolio;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "asset_id", nullable = false)
	private Asset asset;

	@NotNull
	@Column(name = "market_price", nullable = false)
	@DecimalMin(value = "0.0", inclusive = false, message = "Market price must be greater than 0")
	private double marketPrice;

	@Column(name = "order_total_cost", nullable = false)
	@DecimalMin(value = "0.0", inclusive = false, message = "Order total cost must be greater than 0")
	private double orderTotalCost;

	@Column(name = "order_quantity", nullable = false)
	@DecimalMin(value = "0.0", inclusive = false, message = "Order quantity must be greater than 0")
	private double orderQuantity;

	@Column(name = "avg_buy_price_at_moment", nullable = false)
	@DecimalMin(value = "0.0", message = "Average buy price cannot be negative")
	private double avgBuyPriceAtMoment;

	@NotNull
	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.STRING)
	private TransactionType type;

	@Nullable
	@Column(name = "note", length = 250)
	private String note;

	@NotNull
	@Column(nullable = false)
	private LocalDateTime dateTime;

	public Transaction(Asset asset, double marketPrice, double orderTotalCost, TransactionType type) {
		this(asset, marketPrice, orderTotalCost, 0.0, type);
	}

	public Transaction(Asset asset, double marketPrice, double orderTotalCost, double avgBuyPriceAtMoment, TransactionType type) {
		this(asset, marketPrice, orderTotalCost, avgBuyPriceAtMoment, type, null, LocalDateTime.now());
	}

	public Transaction(Asset asset, double marketPrice, double orderTotalCost, double avgBuyPriceAtMoment,
					   TransactionType type, String note, LocalDateTime dateTime)
	{
		this.asset = asset;
		this.marketPrice = marketPrice;
		this.orderTotalCost = orderTotalCost;
		this.orderQuantity = orderTotalCost / marketPrice;
		this.avgBuyPriceAtMoment = avgBuyPriceAtMoment;
		this.type = type;
		this.note = note;
		this.dateTime = dateTime;
	}

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