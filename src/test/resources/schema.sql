-- 데이터베이스 초기화
/*DROP DATABASE IF EXISTS ecommerce_testdb;
CREATE DATABASE ecommerce_testdb;
USE ecommerce_testdb;
*/
-- 테이블 초기화
/*DROP TABLE IF EXISTS pay;
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS member;
*/

-- 1. member 테이블
CREATE TABLE member
(
    member_id   BIGINT       NOT NULL AUTO_INCREMENT, -- 회원 ID (PK)
    login_id    VARCHAR(50)  NOT NULL,                -- 로그인 ID
    password    VARCHAR(255) NOT NULL,                -- 비밀번호 (암호화)
    member_name VARCHAR(50)  NOT NULL,                -- 이름
    email       VARCHAR(100) NOT NULL,                -- 이메일
    addr        VARCHAR(255) NULL,                    -- 주소
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE
        CURRENT_TIMESTAMP,

    PRIMARY KEY (member_id),
    UNIQUE KEY uq_login_id (login_id),
    UNIQUE KEY uq_email (email)
);

-- 2. product 테이블
CREATE TABLE product
(
    product_id     BIGINT       NOT NULL AUTO_INCREMENT, -- 상품 ID (PK)
    product_name   VARCHAR(100) NOT NULL,                -- 상품명
    product_price  INT          NOT NULL,                -- 가격
    stock_quantity INT          NOT NULL DEFAULT 0,      -- 재고 수량
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE
        CURRENT_TIMESTAMP,

    PRIMARY KEY (product_id),
    INDEX          idx_product_name (product_name)       -- 상품명 검색용 인덱스
);

-- 3. orders 테이블
CREATE TABLE orders
(
    order_id     BIGINT      NOT NULL AUTO_INCREMENT,                    -- 주문 ID (PK)
    fk_member_id BIGINT      NOT NULL,                                   -- 회원 ID (FK)
    ordered_at   DATETIME    NOT NULL,                                   -- 주문일 (애플리케이션에서 생성)
    order_status VARCHAR(20) NOT NULL DEFAULT 'ORDERED',                 -- 주문 상태
    total_amount INT         NOT NULL,                                   -- 총 주문 금액 (역정규화)
    created_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE
        CURRENT_TIMESTAMP,

    PRIMARY KEY (order_id),
    INDEX        idx_order_status_ordered_at (order_status, ordered_at), -- 관리자용 주문 조회 인덱스
    INDEX        idx_fk_member_id (fk_member_id)                         -- 외래키 제약 조건 삭제 대신 인덱스 설정
);

-- 4. order_item 테이블
CREATE TABLE order_item(
    order_item_id  BIGINT       NOT NULL AUTO_INCREMENT,      -- 주문 상품 ID (PK)
    fk_order_id    BIGINT       NOT NULL,
    fk_product_id  BIGINT       NOT NULL,                     -- 주문 ID (FK)-- 상품 ID (FK)
    product_name   VARCHAR(100) NOT NULL,
    order_price    INT          NOT NULL,
    order_quantity INT          NOT NULL,                     -- 주문 당시 상품명 (역정규화)-- 주문 당시 가격-- 주문 수량
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE
        CURRENT_TIMESTAMP,

    PRIMARY KEY (order_item_id),
    INDEX          idx_fk_order_item_order (fk_order_id),     -- 외래키 제약 조건 삭제 대신 인덱스 설정
    INDEX          idx_fk_order_item_product (fk_product_id), -- 외래키 제약 조건 삭제 대신 인덱스 설정

    --주문 ID와 상품 ID에 대한 유니크 제약 조건
    CONSTRAINT uq_fk_order_id_fk_product_id UNIQUE (fk_order_id, fk_product_id)
);

-- 5. pay 테이블
CREATE TABLE pay (
    pay_id BIGINT  NOT NULL AUTO_INCREMENT, -- 결제 ID (PK)
    fk_order_id BIGINT  NOT NULL,    -- 주문 ID (FK, Unique)
    pay_method VARCHAR(50) NOT NULL,    -- 결제 수단
    pay_amount INT NOT NULL,    -- 결제 금액
    pay_status  VARCHAR(20) NOT NULL,    -- 결제 상태
    paid_at DATETIME NULL,     -- 결제 완료일
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE
    CURRENT_TIMESTAMP,

    PRIMARY KEY (pay_id),
    UNIQUE KEY uq_pay_order_id (fk_order_id) -- 주문 하나당 결제는 하나
);