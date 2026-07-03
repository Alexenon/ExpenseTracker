SET FOREIGN_KEY_CHECKS = 0;

-- ===============================
-- USERS
-- ===============================
INSERT INTO users (id, username, password, email, active_portfolio_id, last_time_updated, time_created_at)
VALUES
(1, 'john_doe', 'hashed_pass_1', 'john@example.com', NULL, NOW(), NOW()),
(2, 'alice_smith', 'hashed_pass_2', 'alice@example.com', NULL, NOW(), NOW());

-- ===============================
-- USER ROLES
-- ===============================
INSERT INTO user_roles (user_id, role) VALUES
(1, 'USER_ROLE'),
(2, 'USER_ROLE');

-- ===============================
-- CATEGORIES
-- ===============================
INSERT INTO categories (id, name) VALUES
(1, 'Food'),
(2, 'Utilities');

-- ===============================
-- ASSETS
-- ===============================
INSERT INTO assets (
    id, symbol, full_name, market_price, change_percentage,
    summary_description, total_market_cap, total_supply,
    circulation_supply, today_volume, image_url
) VALUES
(1, 'BTC', 'Bitcoin', 40000, 1.5, 'Digital gold',
 19000000000000, 21000000, 19500000, 35000000000, 'btc.png'),
(2, 'ETH', 'Ethereum', 2200, 2.0, 'Smart contracts',
 9000000000000, 120000000, 118000000, 20000000000, 'eth.png');

-- ===============================
-- PORTFOLIOS
-- ===============================
INSERT INTO portfolios (id, name, user_id, last_time_updated, time_created_at)
VALUES
(1, 'John Portfolio', 1, NOW(), NOW()),
(2, 'Alice Portfolio', 2, NOW(), NOW());

-- ===============================
-- ACTIVE PORTFOLIOS
-- ===============================
UPDATE users SET active_portfolio_id = 1 WHERE id = 1;
UPDATE users SET active_portfolio_id = 2 WHERE id = 2;

-- ===============================
-- EXPENSES
-- ===============================
INSERT INTO expenses (
    id, name, amount, description, start_date,
    expire_date, `timestamp`, category_id, user_id
) VALUES
(1, 'Groceries', 120, 'Weekly groceries', CURDATE(), NULL, 'WEEKLY', 1, 1),
(2, 'Electricity', 75, 'Monthly bill', CURDATE(), NULL, 'MONTHLY', 2, 2);


-- ===============================
-- TRANSACTIONS (REALISTIC & CONSISTENT)
-- ===============================
INSERT INTO transactions (
    id,
    portfolio_id,
    asset_id,
    market_price,
    order_total_cost,
    order_quantity,
    avg_buy_price_at_moment,
    `type`,
    note,
    date_time
) VALUES
-- BTC (JOHN) - PROGRESSIVE BUY & AVERAGING
(1, 1, 1, 50000, 2500, 0.05000000, 0.00,    'BUY', 'Initial BTC buy', NOW()),
(2, 1, 1, 43000, 2150, 0.05000000, 50000.0,'BUY', 'BTC dip buy',     NOW()),
(3, 1, 1, 42000, 2100, 0.05000000, 46500.0,'BUY', 'BTC dip buy',     NOW()),
(4, 1, 1, 40000, 2000, 0.05000000, 45000.0,'BUY', 'BTC dip buy',     NOW()),
(5, 1, 1, 38000, 1900, 0.05000000, 43750.0,'BUY', 'BTC dip buy',     NOW()),
(6, 1, 1, 35000, 1750, 0.05000000, 42600.0,'BUY', 'BTC strong dip',  NOW()),
-- ETH (ALICE) - ACCUMULATION + PARTIAL SELL
(7, 2, 2, 2200,  4400, 2.00000000, 0.00,    'BUY',  'Initial ETH buy',   NOW()),
(8, 2, 2, 2100,  2100, 1.00000000, 2200.0, 'BUY',  'ETH accumulation', NOW()),
(9, 2, 2, 2050,  2050, 1.00000000, 2166.67,'BUY',  'ETH accumulation', NOW()),
(10,2, 2, 2600,  2600, 1.00000000, 2137.50,'SELL', 'Partial ETH profit', NOW());



-- ===============================
-- ASSET BALANCES (UPDATED, CONSISTENT)
-- ===============================
-- John: BTC
-- Bought: 0.05 @ 40000 + 0.05 @ 38000 = 0.10 BTC
-- Avg buy ≈ 39000 | Cost = 3900
INSERT INTO asset_balances (
    id, portfolio_id, asset_id, amount,
    avg_buy_price, avg_sell_price,
    cost, realized, holding_days,
    last_time_updated, time_created_at
) VALUES
(1, 1, 1, 0.10, 39000, 0, 3900, 0, 60, NOW(), NOW());

-- Alice: ETH
-- Bought: 2 + 1 = 3 ETH
-- Sold: 1 ETH @ 2600
-- Remaining: 2 ETH | Realized profit ≈ 433.33
INSERT INTO asset_balances VALUES
(2, 2, 2, 2.0, 2166.67, 2600, 4333.34, 433.33, 45, NOW(), NOW());

-- ===============================
-- ASSET WATCHERS
-- ===============================
INSERT INTO asset_watchers (
    id, portfolio_id, asset_id,
    target_price, target_amount,
    transaction_type, is_completed
) VALUES
(1, 1, 1, 36000, 1500, 'BUY', 0),
(2, 2, 2, 2800, 1.0, 'SELL', 0);

-- ===============================
-- USER ASSETS
-- ===============================
INSERT INTO user_assets (
    id, user_id, asset_id,
    comment, marked_as_favorite, last_time_updated
) VALUES
(1, 1, 1, 'BTC long-term investment', 1, NOW()),
(2, 2, 2, 'ETH active trading', 1, NOW());

SET FOREIGN_KEY_CHECKS = 1;
