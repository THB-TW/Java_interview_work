USE fin_like_system;

INSERT INTO users (user_id, user_name, email, account) VALUES
('A1236456789', '王小明', 'wang@email.com', '1111999666'),
('B1234567890', '陳小華', 'chen@email.com', '2222888899');

INSERT INTO products (product_name, price, fee_rate) VALUES
('台股基金A', 10000.00, 0.010),
('美債ETF', 35000.00, 0.001),
('高收益債券基金', 20000.00, 0.015);

INSERT INTO like_list (user_id, product_no, purchase_quantity, account, total_fee, total_amount) VALUES
('A1236456789', 2, 1, '1111999666', 200.00, 20200.00),
('B1234567890', 1, 2, '2222888899', 35.00, 35035.00);