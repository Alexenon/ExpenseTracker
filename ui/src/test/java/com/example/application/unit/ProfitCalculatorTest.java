package com.example.application.unit;

import com.example.application.transaction.TransactionDTO;
import com.example.application.transaction.TransactionType;
import com.example.application.utils.investment.ProfitCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ProfitCalculatorTest {

	/*
		Buy Transactions Summary
			___________________________________________________
			| Buy # | Tokens Bought | Price  | Avg Buy Price  |
			|-------|---------------|--------|----------------|
			| 1     | 15            | $0.50  | $0.5000        |
			| 2     | 10            | $0.60  | $0.5400        |
			| 3     | 20            | $0.55  | $0.5444        |
			| 4     | 5             | $0.40  | $0.5300        |
			---------------------------------------------------
	 */
	@Test
	public void testAverageBuyPrice() {
		BigDecimal avg1 = calculateAvgPrice(0, 0, 15, 0.50);
		assertThat(avg1)
				.as("Avg after first DYDX buy")
				.isEqualByComparingTo("0.50");

		BigDecimal avg2 = calculateAvgPrice(avg1, 15, 10, 0.60);
		assertThat(avg2)
				.as("Avg after second DYDX buy")
				.isEqualByComparingTo("0.54");

		BigDecimal avg3 = calculateAvgPrice(avg2, 25, 20, 0.55);
		assertThat(avg3)
				.as("Avg after third DYDX buy")
				.isEqualByComparingTo("0.5444");

		BigDecimal avg4 = calculateAvgPrice(avg3, 45, 5, 0.40);
		assertThat(avg4)
				.as("Avg after fourth DYDX buy")
				.isEqualByComparingTo("0.53");
	}

	/*
	 	Sell Transactions Summary
	 		__________________________________________________
			| Sell # | Tokens Sold | Price  | Avg Sell Price |
			|--------|-------------|--------|----------------|
			| 1 	 | 10 	       | $0.70  | $0.7000 		 |
			| 2 	 | 5  	       | $0.65  | $0.6833 		 |
			| 3 	 | 15 	       | $0.72  | $0.7017 		 |
			| 4 	 | 10 	       | $0.68  | $0.6963 		 |
			__________________________________________________
	 */

	@Test
	public void testDydxAverageSellPriceIn4Steps() {
		BigDecimal avgSell1 = calculateAvgPrice(0, 0, 10, 0.70);
		assertThat(avgSell1)
				.as("Avg after 1st DYDX sell")
				.isEqualByComparingTo("0.70");

		BigDecimal avgSell2 = calculateAvgPrice(avgSell1, 10, 5, 0.65);
		assertThat(avgSell2)
				.as("Avg after 2nd DYDX sell")
				.isEqualByComparingTo("0.6833");

		BigDecimal avgSell3 = calculateAvgPrice(avgSell2, 15, 15, 0.72);
		assertThat(avgSell3)
				.as("Avg after 3rd DYDX sell")
				.isEqualByComparingTo("0.7017");

		BigDecimal avgSell4 = calculateAvgPrice(avgSell3, 30, 10, 0.68);
		assertThat(avgSell4)
				.as("Avg after 4th DYDX sell")
				.isEqualByComparingTo("0.6963");
	}

	@Test
	void realizedProfit_shouldReturnZero_whenOnlyBuys() {
		List<TransactionDTO> transactions = List.of(
				buy(10, 100),
				buy(5, 200)
		);

		BigDecimal result = ProfitCalculator.realizedProfit(transactions);

		assertThat(result).isEqualByComparingTo("0");
	}

	@Test
	void realizedProfit_shouldCalculateProfit_forSingleBuySell() {
		List<TransactionDTO> transactions = List.of(
				buy(10, 100),
				sell(10, 150)
		);

		BigDecimal result = ProfitCalculator.realizedProfit(transactions);

		assertThat(result).isEqualByComparingTo("500.00000000");
	}

	@Test
	void realizedProfit_shouldUseAverageCost_whenMultipleBuys() {
		List<TransactionDTO> transactions = List.of(
				buy(10, 100),
				buy(10, 200),
				sell(10, 300)
		);

		BigDecimal result = ProfitCalculator.realizedProfit(transactions);

		assertThat(result).isEqualByComparingTo("1500.00000000");
	}

	@Test
	void realizedProfit_shouldHandlePartialSell() {
		List<TransactionDTO> transactions = List.of(
				buy(10, 100),
				sell(5, 200)
		);

		BigDecimal result = ProfitCalculator.realizedProfit(transactions);

		assertThat(result).isEqualByComparingTo("500.00000000");
	}

	@Test
	void realizedProfit_shouldHandleMultipleSells() {
		List<TransactionDTO> transactions = List.of(
				buy(10, 100),
				sell(5, 200),
				sell(5, 300)
		);

		BigDecimal result = ProfitCalculator.realizedProfit(transactions);

		assertThat(result).isEqualByComparingTo("1500.00000000");
	}

	@Test
	void realizedProfit_shouldThrowException_whenSellingMoreThanOwned() {
		List<TransactionDTO> transactions = List.of(
				buy(5, 100),
				sell(10, 200)
		);

		assertThatThrownBy(() -> ProfitCalculator.realizedProfit(transactions))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Selling more than owned");
	}

	// -------- helper builders --------

	private TransactionDTO buy(double qty, double price) {
		TransactionDTO dto = new TransactionDTO();
		dto.setType(TransactionType.BUY);
		dto.setOrderQuantity(BigDecimal.valueOf(qty));
		dto.setMarketPrice(BigDecimal.valueOf(price));
		dto.setDateTime(LocalDateTime.now());
		return dto;
	}

	private TransactionDTO sell(double qty, double price) {
		TransactionDTO dto = new TransactionDTO();
		dto.setType(TransactionType.SELL);
		dto.setOrderQuantity(BigDecimal.valueOf(qty));
		dto.setMarketPrice(BigDecimal.valueOf(price));
		dto.setDateTime(LocalDateTime.now());
		return dto;
	}

	private static BigDecimal calculateAvgPrice(BigDecimal prevAvg, double prevAmount, double newAmount, double buyPrice) {
		return ProfitCalculator.calculateNewAvgPrice(
				prevAvg,
				BigDecimal.valueOf(prevAmount),
				BigDecimal.valueOf(newAmount),
				BigDecimal.valueOf(buyPrice)
		);
	}

	private static BigDecimal calculateAvgPrice(double prevAvg, double prevAmount, double newAmount, double buyPrice) {
		return ProfitCalculator.calculateNewAvgPrice(
				BigDecimal.valueOf(prevAvg),
				BigDecimal.valueOf(prevAmount),
				BigDecimal.valueOf(newAmount),
				BigDecimal.valueOf(buyPrice)
		);
	}
}