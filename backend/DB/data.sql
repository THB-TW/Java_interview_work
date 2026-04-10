-- ============================================================
-- fin_like_system Initial Data
-- 執行前請先執行 schema.sql
-- 密碼明文皆為 password123，以 BCrypt ($2a$10$) 雜湊儲存
-- ============================================================

USE fin_like_system;

-- ----------------------------------------------------------
-- users
-- ----------------------------------------------------------
INSERT INTO users (user_id, user_name, email, account) VALUES
('A1236456789', '王小明', 'wang@email.com',  '1111999666'),
('B1234567890', '陳小華', 'chen@email.com',  '2222888899');

-- ----------------------------------------------------------
-- user_credentials
--   password123  →  $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
-- ----------------------------------------------------------
INSERT INTO user_credentials (user_id, username, password) VALUES
('A1236456789', 'wangxiaoming', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('B1234567890', 'chenxiaohua',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

-- ----------------------------------------------------------
-- products
--   product_no 由 AUTO_INCREMENT 產生（1, 2, 3）
--   total_fee  = price * purchase_quantity * fee_rate
-- ----------------------------------------------------------
INSERT INTO products (product_name, price, fee_rate) VALUES
('台股基金A',    10000.00, 0.0100),   -- product_no = 1
('美債ETF',      35000.00, 0.0010),   -- product_no = 2
('高收益債券基金', 20000.00, 0.0150); -- product_no = 3

-- ----------------------------------------------------------
-- like_list
--   計算說明：
--     sn=1: 美債ETF  × 1 qty → fee=35000×1×0.001=35.00,  amount=35000+35=35035.00
--     sn=2: 台股基金A × 2 qty → fee=10000×2×0.010=200.00, amount=20000+200=20200.00
--     sn=3: 台股基金A × 2 qty → fee=10000×2×0.010=200.00, amount=20000+200=20200.00
-- ----------------------------------------------------------
INSERT INTO like_list (user_id, product_no, purchase_quantity, account, total_fee, total_amount) VALUES
('A1236456789', 2, 1, '1111999666',  35.00, 35035.00),
('A1236456789', 1, 2, '1111999666', 200.00, 20200.00),
('B1234567890', 1, 2, '2222888899', 200.00, 20200.00);