USE fin_like_system;

DROP PROCEDURE IF EXISTS sp_add_like;
DROP PROCEDURE IF EXISTS sp_get_like_list;
DROP PROCEDURE IF EXISTS sp_update_like;
DROP PROCEDURE IF EXISTS sp_delete_like;

DELIMITER $$

CREATE PROCEDURE sp_add_like (
    IN i_user_id VARCHAR(20),
    IN i_product_no BIGINT,
    IN i_purchase_quantity INT,
    IN i_account VARCHAR(20)
)
BEGIN
    DECLARE add_price DECIMAL(15,2);
    DECLARE add_fee_rate DECIMAL(5,4);
    DECLARE add_total_fee DECIMAL(15,2);
    DECLARE add_total_amount DECIMAL(15,2);

    SELECT price, fee_rate
    INTO add_price, add_fee_rate
    FROM products
    WHERE product_no = i_product_no;

    SET add_total_fee = (add_price * i_purchase_quantity) * add_fee_rate;
    SET add_total_amount = (add_price * i_purchase_quantity) + add_total_fee;

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
        add_total_fee,
        add_total_amount
    );
END$$

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
    FROM like_list l
    WHERE l.user_id = i_user_id
    ORDER BY l.sn;
END$$

CREATE PROCEDURE sp_update_like (
    IN i_sn BIGINT,
    IN i_user_id VARCHAR(20),
    IN i_product_no BIGINT,
    IN i_purchase_quantity INT,
    IN i_account VARCHAR(20)
)
BEGIN
    DECLARE update_price DECIMAL(15,2);
    DECLARE update_fee_rate DECIMAL(5,4);
    DECLARE update_total_fee DECIMAL(15,2);
    DECLARE update_total_amount DECIMAL(15,2);

    SELECT price, fee_rate
    INTO update_price, update_fee_rate
    FROM products
    WHERE product_no = i_product_no;

    SET update_total_fee = (update_price * i_purchase_quantity) * update_fee_rate;
    SET update_total_amount = (update_price * i_purchase_quantity) + update_total_fee;

    UPDATE like_list
    SET
        purchase_quantity = i_purchase_quantity,
        account = i_account,
        total_fee = update_total_fee,
        total_amount = update_total_amount
    WHERE sn = i_sn AND user_id=i_user_id AND product_no = i_product_no;
END$$

CREATE PROCEDURE sp_delete_like (
   IN i_sn BIGINT,
   IN i_user_id VARCHAR(20)
)
BEGIN
    DELETE FROM like_list
    WHERE sn = i_sn AND user_id = i_user_id;
END$$

DELIMITER ;