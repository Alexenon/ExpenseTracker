package com.example.application;

import com.example.application.utils.investment.ProfitCalculator;
import org.junit.jupiter.api.Test;

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
		double avg1 = ProfitCalculator.calculateNewAvgPrice(0, 0, 15, 0.50);
		assertEquals(0.50, avg1, PRICE_DELTA, "Avg after first DYDX buy");

		// Second buy: 10 tokens at $0.60 → avg = $0.54
		double avg2 = ProfitCalculator.calculateNewAvgPrice(avg1, 15, 10, 0.60);
		assertEquals(0.54, avg2, PRICE_DELTA, "Avg after second DYDX buy");

		// Third buy: 20 tokens at $0.55 → avg = $0.5444
		double avg3 = ProfitCalculator.calculateNewAvgPrice(avg2, 25, 20, 0.55);
		assertEquals(0.5444, avg3, PRICE_DELTA, "Avg after third DYDX buy");

		// Fourth buy: 5 tokens at $0.40 → avg = $0.53
		double avg4 = ProfitCalculator.calculateNewAvgPrice(avg3, 45, 5, 0.40);
		assertEquals(0.53, avg4, PRICE_DELTA, "Avg after fourth DYDX buy");
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
		double avgSell1 = ProfitCalculator.calculateNewAvgPrice(0, 0, 10, 0.70);
		assertEquals(0.70, avgSell1, PRICE_DELTA, "Avg after 1st DYDX sell");

		// Sell 2: 5 tokens at $0.65 → avg = $0.6833
		double avgSell2 = ProfitCalculator.calculateNewAvgPrice(avgSell1, 10, 5, 0.65);
		assertEquals(0.6833, avgSell2, PRICE_DELTA, "Avg after 2nd DYDX sell");

		// Sell 3: 15 tokens at $0.72 → avg = $0.7017
		double avgSell3 = ProfitCalculator.calculateNewAvgPrice(avgSell2, 15, 15, 0.72);
		assertEquals(0.7017, avgSell3, PRICE_DELTA, "Avg after 3rd DYDX sell");

		// Sell 4: 10 tokens at $0.68 → avg = $0.6963
		double avgSell4 = ProfitCalculator.calculateNewAvgPrice(avgSell3, 30, 10, 0.68);
		assertEquals(0.6963, avgSell4, PRICE_DELTA, "Avg after 4th DYDX sell");
	}


}
