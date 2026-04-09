USE fin_like_system;

INSERT INTO users (user_id, user_name, email, account) VALUES
('A1236456789', '王小明', 'wang@email.com', '1111999666'),
('B1234567890', '陳小華', 'chen@email.com', '2222888899');

-- 密碼皆為 password123，以 BCrypt 雜湊（$2a$10$...）
INSERT INTO user_credentials (user_id, username, password) VALUES
('A1236456789', 'wangxiaoming', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy'),
('B1234567890', 'chenxiaohua',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy');

INSERT INTO products (product_name, price, fee_rate) VALUES
('台股基金A', 10000.00, 0.0100),
('美債ETF', 35000.00, 0.0010),
('高收益債券基金', 20000.00, 0.0150);

INSERT INTO like_list (user_id, product_no, purchase_quantity, account, total_fee, total_amount) VALUES
('A1236456789', 2, 1, '1111999666', 35.00, 35035.00),
('A1236456789', 1, 2, '1111999666', 200.00, 20200.00),
('B1234567890', 1, 2, '2222888899', 200.00, 20200.00);