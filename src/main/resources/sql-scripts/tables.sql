SET FOREIGN_KEY_CHECKS = 0;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    active_portfolio_id BIGINT UNIQUE,
    last_time_updated DATETIME NOT NULL,
    time_created_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (user_id, role),
    CONSTRAINT fk_user_roles_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS assets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(12) NOT NULL UNIQUE,
    full_name VARCHAR(55) NOT NULL,
    market_price DOUBLE NOT NULL,
    change_percentage DOUBLE NOT NULL,
    summary_description VARCHAR(1000) NOT NULL,
    total_market_cap DECIMAL(38,0) NOT NULL,
    total_supply DECIMAL(38,0) NOT NULL,
    circulation_supply DECIMAL(38,0) NOT NULL,
    today_volume DOUBLE NOT NULL,
    image_url VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS portfolios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    user_id BIGINT NOT NULL,
    last_time_updated DATETIME NOT NULL,
    time_created_at DATETIME NOT NULL,
    CONSTRAINT fk_portfolio_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    amount DOUBLE NOT NULL,
    description VARCHAR(255),
    start_date DATE NOT NULL,
    expire_date DATE,
    `timestamp` VARCHAR(20) NOT NULL,
    category_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT fk_expense_category
        FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_expense_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS asset_balances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    portfolio_id BIGINT NOT NULL,
    asset_id BIGINT NOT NULL,
    amount DOUBLE NOT NULL,
    avg_buy_price DOUBLE NOT NULL,
    avg_sell_price DOUBLE NOT NULL,
    cost DOUBLE NOT NULL,
    realized DOUBLE NOT NULL,
    holding_days DOUBLE NOT NULL,
    last_time_updated DATETIME NOT NULL,
    time_created_at DATETIME NOT NULL,
    CONSTRAINT fk_asset_balance_portfolio
        FOREIGN KEY (portfolio_id) REFERENCES portfolios(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_asset_balance_asset
        FOREIGN KEY (asset_id) REFERENCES assets(id)
);

CREATE TABLE IF NOT EXISTS asset_watchers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    portfolio_id BIGINT NOT NULL,
    asset_id BIGINT NOT NULL,
    target_price DOUBLE NOT NULL,
    target_amount DOUBLE NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    is_completed TINYINT(1) NOT NULL,
    CONSTRAINT fk_asset_watcher_portfolio
        FOREIGN KEY (portfolio_id) REFERENCES portfolios(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_asset_watcher_asset
        FOREIGN KEY (asset_id) REFERENCES assets(id)
);

CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    portfolio_id BIGINT NOT NULL,
    asset_id BIGINT NOT NULL,
    market_price DOUBLE NOT NULL,
    order_total_cost DOUBLE NOT NULL,
    order_quantity DOUBLE NOT NULL,
    avg_buy_price_at_moment DOUBLE NOT NULL,
    `type` VARCHAR(20) NOT NULL,
    note VARCHAR(250),
    date_time DATETIME NOT NULL,
    CONSTRAINT fk_transaction_portfolio
        FOREIGN KEY (portfolio_id) REFERENCES portfolios(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_transaction_asset
        FOREIGN KEY (asset_id) REFERENCES assets(id)
);

CREATE TABLE IF NOT EXISTS user_assets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    asset_id BIGINT NOT NULL,
    comment VARCHAR(255),
    marked_as_favorite TINYINT(1) NOT NULL,
    last_time_updated DATETIME NOT NULL,
    CONSTRAINT fk_user_asset_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_asset_asset
        FOREIGN KEY (asset_id) REFERENCES assets(id)
);

ALTER TABLE users
ADD CONSTRAINT fk_user_active_portfolio
FOREIGN KEY (active_portfolio_id)
REFERENCES portfolios(id);

SET FOREIGN_KEY_CHECKS = 1;
