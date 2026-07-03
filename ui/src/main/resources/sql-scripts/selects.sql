-------------------------------------------------------------------------------------------------------
--  1
-------------------------------------------------------------------------------------------------------


1. Show all portfolios that have generated more than 15% total return and contain at least 5 different
assets with a combined market value exceeding 50,000.
Display portfolio name, user username, number of assets held,
	total portfolio value (current_market_price * amount tokens),
	total realized gains,
	total unrealized gains (based on current market prices vs. average buy price),
	and percentage return.
Order by percentage return in descending order.


SELECT
    p.name AS portfolio_name,
    u.username,
    COUNT(DISTINCT ab.asset_id) AS nr_assets,
    SUM(a.market_price * ab.amount) AS total_portfolio_value,
    SUM(ab.realized) AS total_realized_gains,
    SUM((a.market_price - ab.avg_buy_price) * ab.amount) AS total_unrealized_gains,
	((SUM(a.market_price * ab.amount) - SUM(ab.cost) + SUM(ab.realized)) / SUM(ab.cost)) * 100
    ) AS percentage_return
FROM portfolios p
JOIN users u ON u.id = p.user_id
JOIN asset_balances ab ON ab.portfolio_id = p.id
JOIN assets a ON a.id = ab.asset_id
GROUP BY p.id, p.name, u.username
HAVING
    percentage_return > 15
    AND COUNT(DISTINCT ab.asset_id) >= 5
    AND total_portfolio_value > 50000
ORDER BY percentage_return DESC;


-------------------------------------------------------------------------------------------------------
--  2
-------------------------------------------------------------------------------------------------------


2. Display all users who have executed more than 30 transactions in the last 3 months across at least 3 different
asset categories and have set up at least 2 active asset watchers.
Show username, email, user role, number of transactions, number of different assets traded,
total transaction volume, number of expense categories used, number of active watchers, and average holding period (in days).
Filter only users with total transaction volume above 20,000. Order by transaction count descending.


SELECT
    u.username,
    u.email,
    ur.role,
    COUNT(DISTINCT t.id) AS nr_transactions,
    COUNT(DISTINCT t.asset_id) AS different_assets_traded,
    SUM(t.order_total_cost) AS total_transaction_volume,
    COUNT(DISTINCT aw.id) AS active_watchers,
    AVG(ab.holding_days) AS avg_holding_days
FROM users u
JOIN user_roles ur ON ur.user_id = u.id
JOIN portfolios p ON p.user_id = u.id
JOIN transactions t ON t.portfolio_id = p.id
LEFT JOIN asset_watchers aw ON aw.portfolio_id = p.id AND aw.is_completed = 0
LEFT JOIN asset_balances ab ON ab.portfolio_id = p.id
WHERE t.date_time >= DATE_SUB(NOW(), INTERVAL 3 MONTH)
GROUP BY u.id, u.username, u.email, ur.role
HAVING
    COUNT(DISTINCT t.id) > 30
    AND COUNT(DISTINCT aw.id) >= 2
    AND SUM(t.order_total_cost) > 20000
ORDER BY nr_transactions DESC;



-------------------------------------------------------------------------------------------------------
--  3
-------------------------------------------------------------------------------------------------------


3. Find all assets that have been traded more than 10 times, have a market cap above 1 million,
and show a price change percentage greater than 5% (positive or negative).
Display asset symbol, full name, current market price, percentage change, total market cap,
	circulation supply, trading volume (sum of transaction quantities), number of unique holders (users),
	average buy price across all transactions, and price volatility score
	(based on avg_buy_price_at_moment variance).
order by trading volume descending.


SELECT
    a.symbol,
    a.full_name,
    a.market_price,
    a.change_percentage,
    a.total_market_cap,
    a.circulation_supply,
    SUM(t.order_quantity) AS trading_volume,
    COUNT(DISTINCT p.user_id) AS unique_holders,
    AVG(t.avg_buy_price_at_moment) AS average_buy_price
FROM assets a
JOIN transactions t ON t.asset_id = a.id
JOIN portfolios p ON p.id = t.portfolio_id
GROUP BY a.id
HAVING
    COUNT(t.id) > 10
    AND a.total_market_cap > 1000000
    AND (a.change_percentage > 5 OR a.change_percentage < -5)
ORDER BY trading_volume DESC;



-------------------------------------------------------------------------------------------------------
--  4
-------------------------------------------------------------------------------------------------------


4 Calculate spending patterns for users who have recorded at least 10 expenses in the last 6 months.
For each user and expense category combination, show username, category name, number of expenses, total amount spent,
	average expense amount, date range (start_date to expire_date), percentage of total spending in this category,
	and compare against similar users in the same role. Show only categories where spending exceeds 1,000.
Order by total spending per category descending.



SELECT
    u.username,
    c.name AS category_name,
    COUNT(e.id) AS nr_expenses,
    SUM(e.amount) AS total_spent,
    AVG(e.amount) AS average_spent,
    MIN(e.start_date) AS range_start_date,
    MAX(e.expire_date) AS range_end_date,
    (SUM(e.amount) / SUM(SUM(e.amount)) OVER (PARTITION BY u.id)) * 100 AS percentage_of_user_spending
FROM users u
JOIN expenses e ON e.user_id = u.id
JOIN categories c ON c.id = e.category_id
WHERE
    e.start_date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
GROUP BY u.id, u.username, c.id, c.name
HAVING total_spent > 1000
ORDER BY total_spent DESC;



-------------------------------------------------------------------------------------------------------
--  5
-------------------------------------------------------------------------------------------------------



5. Identify assets that are being watched by more than 10 users, where the target price is at least
10% different from the current market price, and the watcher completion rate is less than 30%.
Display asset symbol, asset name, current market price, average target price from watchers,
	percentage difference, number of active watchers, target amount sum,
	action type distribution (buy/sell), number of completed watchers vs. active ones,
	and average holding days for users who own this asset.
Show assets with the highest potential opportunity (largest target-to-market gap).
Order by number of watchers descending.



SELECT
    a.symbol,
    a.full_name AS asset_name,
    a.market_price AS current_market_price,
    AVG(aw.target_price) AS average_target_price,
    ROUND(((AVG(aw.target_price) - a.market_price) / a.market_price) * 100, 2) AS percentage_difference,
    COUNT(CASE WHEN aw.is_completed = 0 THEN 1 END) AS number_active_watchers,
    SUM(aw.target_amount) AS target_amount_sum,
    SUM(CASE WHEN aw.transaction_type = 'BUY' THEN 1 ELSE 0 END) AS buy_watchers,
    SUM(CASE WHEN aw.transaction_type = 'SELL' THEN 1 ELSE 0 END) AS sell_watchers,
    SUM(CASE WHEN aw.is_completed = 1 THEN 1 ELSE 0 END) AS completed_watchers,
    COUNT(CASE WHEN aw.is_completed = 0 THEN 1 END) AS active_watchers,
    ROUND(AVG(aw.is_completed) * 100, 2) AS completion_rate_percentage,
    AVG(ab.holding_days) AS average_holding_days,
    COUNT(DISTINCT aw.portfolio_id) AS unique_watchers
FROM assets a
JOIN asset_watchers aw ON aw.asset_id = a.id
LEFT JOIN asset_balances ab ON ab.asset_id = a.id
GROUP BY a.id, a.symbol, a.full_name, a.market_price
HAVING
    COUNT(DISTINCT aw.portfolio_id) > 10
    AND ROUND(AVG(aw.is_completed) * 100, 2) < 30
    AND (
        AVG(aw.target_price) < a.market_price * 0.9
        OR AVG(aw.target_price) > a.market_price * 1.1
    )
ORDER BY unique_watchers DESC;










