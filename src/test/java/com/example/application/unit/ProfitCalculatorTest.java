package com.example.application.unit;

import com.example.application.utils.investment.ProfitCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProfitCalculatorTest {

	public static final double PRICE_DELTA = 0.0001;

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
		// First buy: 15 tokens at $0.50
		BigDecimal avg1 = calculateAvgPrice(0, 0, 15, 0.50);
		assertEquals(BigDecimal.valueOf(0.50), avg1, "Avg after first DYDX buy");

		// Second buy: 10 tokens at $0.60 → avg = $0.54
		BigDecimal avg2 = calculateAvgPrice(avg1, 15, 10, 0.60);
		assertEquals(BigDecimal.valueOf(0.54), avg2, "Avg after second DYDX buy");

		// Third buy: 20 tokens at $0.55 → avg = $0.5444
		BigDecimal avg3 = calculateAvgPrice(avg2, 25, 20, 0.55);
		assertEquals(BigDecimal.valueOf(0.5444), avg3, "Avg after third DYDX buy");

		// Fourth buy: 5 tokens at $0.40 → avg = $0.53
		BigDecimal avg4 = calculateAvgPrice(avg3, 45, 5, 0.40);
		assertEquals(BigDecimal.valueOf(0.53), avg4, "Avg after fourth DYDX buy");
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
		// Sell 1: 10 tokens at $0.70
		BigDecimal avgSell1 = calculateAvgPrice(0, 0, 10, 0.70);
		assertEquals(BigDecimal.valueOf(0.70), avgSell1, "Avg after 1st DYDX sell");

		// Sell 2: 5 tokens at $0.65 → avg = $0.6833
		BigDecimal avgSell2 = calculateAvgPrice(avgSell1, 10, 5, 0.65);
		assertEquals(BigDecimal.valueOf(0.6833), avgSell2, "Avg after 2nd DYDX sell");

		// Sell 3: 15 tokens at $0.72 → avg = $0.7017
		BigDecimal avgSell3 = calculateAvgPrice(avgSell2, 15, 15, 0.72);
		assertEquals(BigDecimal.valueOf(0.7017), avgSell3, "Avg after 3rd DYDX sell");

		// Sell 4: 10 tokens at $0.68 → avg = $0.6963
		BigDecimal avgSell4 = calculateAvgPrice(avgSell3, 30, 10, 0.68);
		assertEquals(BigDecimal.valueOf(0.6963), avgSell4, "Avg after 4th DYDX sell");
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
