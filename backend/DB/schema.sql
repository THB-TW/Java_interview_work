-- ============================================================
-- fin_like_system Schema
-- Database: MariaDB
-- Charset:  utf8mb4 / utf8mb4_unicode_ci
-- ============================================================

CREATE DATABASE IF NOT EXISTS fin_like_system
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE fin_like_system;

-- Drop tables in reverse-dependency order
DROP TABLE IF EXISTS like_list;
DROP TABLE IF EXISTS user_credentials;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS users;

-- ----------------------------------------------------------
-- Table: users
--   儲存使用者基本資料（身分證字號作 PK）
-- ----------------------------------------------------------
CREATE TABLE users (
    user_id    VARCHAR(20)  NOT NULL COMMENT '使用者ID（身分證字號）',
    user_name  VARCHAR(50)  NOT NULL COMMENT '使用者姓名',
    email      VARCHAR(100) NOT NULL COMMENT '電子郵件',
    account    VARCHAR(20)  NOT NULL COMMENT '銀行扣款帳號',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------
-- Table: user_credentials
--   儲存登入帳號與 BCrypt 雜湊密碼
-- ----------------------------------------------------------
CREATE TABLE user_credentials (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    VARCHAR(20)  NOT NULL COMMENT '對應 users.user_id',
    username   VARCHAR(50)  NOT NULL COMMENT '登入帳號',
    password   VARCHAR(255) NOT NULL COMMENT 'BCrypt 雜湊密碼',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_user_credentials_user_id  (user_id),
    UNIQUE KEY uq_user_credentials_username (username),
    CONSTRAINT fk_user_credentials_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------
-- Table: products
--   金融商品清單
-- ----------------------------------------------------------
CREATE TABLE products (
    product_no   BIGINT         NOT NULL AUTO_INCREMENT COMMENT '商品編號',
    product_name VARCHAR(100)   NOT NULL COMMENT '商品名稱',
    price        DECIMAL(15,2)  NOT NULL COMMENT '商品單價',
    fee_rate     DECIMAL(5,4)   NOT NULL COMMENT '手續費率（例如 0.0100 = 1%）',
    created_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (product_no)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------
-- Table: like_list
--   使用者喜好清單（申購紀錄）
--   total_fee    = price * purchase_quantity * fee_rate
--   total_amount = price * purchase_quantity + total_fee
-- ----------------------------------------------------------
CREATE TABLE like_list (
    sn                BIGINT        NOT NULL AUTO_INCREMENT COMMENT '序號',
    user_id           VARCHAR(20)   NOT NULL COMMENT '對應 users.user_id',
    product_no        BIGINT        NOT NULL COMMENT '對應 products.product_no',
    purchase_quantity INT           NOT NULL COMMENT '申購數量',
    account           VARCHAR(20)   NOT NULL COMMENT '扣款帳號',
    total_fee         DECIMAL(15,2) NOT NULL COMMENT '手續費',
    total_amount      DECIMAL(15,2) NOT NULL COMMENT '申購總金額（含費用）',
    created_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (sn),
    CONSTRAINT fk_like_list_user    FOREIGN KEY (user_id)    REFERENCES users(user_id),
    CONSTRAINT fk_like_list_product FOREIGN KEY (product_no) REFERENCES products(product_no)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci;