-- ============================================================
-- fin_like_system Stored Procedures
-- 執行前請先執行 schema.sql
-- ============================================================

USE fin_like_system;

DROP PROCEDURE IF EXISTS sp_add_like;
DROP PROCEDURE IF EXISTS sp_get_like_list;
DROP PROCEDURE IF EXISTS sp_update_like;
DROP PROCEDURE IF EXISTS sp_delete_like;

DELIMITER $$

-- ----------------------------------------------------------
-- sp_add_like
--   新增一筆喜好記錄（自動計算 total_fee / total_amount）
--
--   i_user_id          VARCHAR(20) - 使用者ID（對應 users.user_id）
--   i_product_no       BIGINT      - 商品編號（對應 products.product_no）
--   i_purchase_quantity INT        - 申購數量（必須 > 0）
--   i_account          VARCHAR(20) - 扣款帳號
--
--   計算公式：
--     total_fee    = price × purchase_quantity × fee_rate
--     total_amount = price × purchase_quantity + total_fee
-- ----------------------------------------------------------
CREATE PROCEDURE sp_add_like (
    IN i_user_id           VARCHAR(20),
    IN i_product_no        BIGINT,
    IN i_purchase_quantity INT,
    IN i_account           VARCHAR(20)
)
BEGIN
    DECLARE v_price        DECIMAL(15,2);
    DECLARE v_fee_rate     DECIMAL(5,4);
    DECLARE v_total_fee    DECIMAL(15,2);
    DECLARE v_total_amount DECIMAL(15,2);

    SELECT price, fee_rate
      INTO v_price, v_fee_rate
      FROM products
     WHERE product_no = i_product_no;

    SET v_total_fee    = (v_price * i_purchase_quantity) * v_fee_rate;
    SET v_total_amount = (v_price * i_purchase_quantity) + v_total_fee;

    INSERT INTO like_list (
        user_id,
        product_no,
        purchase_quantity,
        account,
        total_fee,
        total_amount
    ) VALUES (
        i_user_id,
        i_product_no,
        i_purchase_quantity,
        i_account,
        v_total_fee,
        v_total_amount
    );
END$$

-- ----------------------------------------------------------
-- sp_get_like_list
--   查詢指定使用者的喜好清單
--   回傳欄位對應 LikeRepository.LikeRowMapper：
--     sn, product_no, purchase_quantity, account, total_fee, total_amount
--
--   i_user_id VARCHAR(20) - 使用者ID
-- ----------------------------------------------------------
CREATE PROCEDURE sp_get_like_list (
    IN i_user_id VARCHAR(20)
)
BEGIN
    SELECT
        sn,
        product_no,
        purchase_quantity,
        account,
        total_fee,
        total_amount
    FROM like_list
    WHERE user_id = i_user_id
    ORDER BY sn;
END$$

-- ----------------------------------------------------------
-- sp_update_like
--   修改指定喜好記錄（重新計算 total_fee / total_amount）
--   同時以 sn + user_id + product_no 確保資料所有權
--
--   i_sn               BIGINT      - 記錄序號
--   i_user_id          VARCHAR(20) - 使用者ID
--   i_product_no       BIGINT      - 商品編號
--   i_purchase_quantity INT        - 新申購數量（必須 > 0）
--   i_account          VARCHAR(20) - 新扣款帳號
-- ----------------------------------------------------------
CREATE PROCEDURE sp_update_like (
    IN i_sn                BIGINT,
    IN i_user_id           VARCHAR(20),
    IN i_product_no        BIGINT,
    IN i_purchase_quantity INT,
    IN i_account           VARCHAR(20)
)
BEGIN
    DECLARE v_price        DECIMAL(15,2);
    DECLARE v_fee_rate     DECIMAL(5,4);
    DECLARE v_total_fee    DECIMAL(15,2);
    DECLARE v_total_amount DECIMAL(15,2);

    SELECT price, fee_rate
      INTO v_price, v_fee_rate
      FROM products
     WHERE product_no = i_product_no;

    SET v_total_fee    = (v_price * i_purchase_quantity) * v_fee_rate;
    SET v_total_amount = (v_price * i_purchase_quantity) + v_total_fee;

    UPDATE like_list
       SET purchase_quantity = i_purchase_quantity,
           account           = i_account,
           total_fee         = v_total_fee,
           total_amount      = v_total_amount
     WHERE sn         = i_sn
       AND user_id    = i_user_id
       AND product_no = i_product_no;
END$$

-- ----------------------------------------------------------
-- sp_delete_like
--   刪除指定喜好記錄（以 sn + user_id 確保資料所有權）
--
--   i_sn      BIGINT      - 記錄序號
--   i_user_id VARCHAR(20) - 使用者ID
-- ----------------------------------------------------------
CREATE PROCEDURE sp_delete_like (
    IN i_sn      BIGINT,
    IN i_user_id VARCHAR(20)
)
BEGIN
    DELETE FROM like_list
     WHERE sn      = i_sn
       AND user_id = i_user_id;
END$$

DELIMITER ;