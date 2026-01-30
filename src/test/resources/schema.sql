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
    created_at  TIMESTAMP     NOT NULL,
    updated_at  TIMESTAMP     NOT NULL,

    PRIMARY KEY (member_id),
    CONSTRAINT uq_login_id UNIQUE (login_id),
    CONSTRAINT uq_email UNIQUE (email)
);

-- 2. product 테이블
CREATE TABLE product
(
    product_id     BIGINT       NOT NULL AUTO_INCREMENT, -- 상품 ID (PK)
    product_name   VARCHAR(100) NOT NULL,                -- 상품명
    product_price  INT          NOT NULL,                -- 가격
    product_status    VARCHAR(10)  NOT NULL,
    stock_quantity INT          NOT NULL DEFAULT 0,      -- 재고 수량
    created_at     TIMESTAMP     NOT NULL,
    updated_at     TIMESTAMP     NOT NULL,

    PRIMARY KEY (product_id)
);

CREATE INDEX idx_product_name
    ON product (product_name);  -- 상품명 검색용 인덱스

-- 3. orders 테이블
CREATE TABLE orders
(
    order_id     BIGINT      NOT NULL AUTO_INCREMENT,                    -- 주문 ID (PK)
    fk_member_id BIGINT      NOT NULL,                                   -- 회원 ID (FK)
    pay_order_id VARCHAR(64) NOT NULL,                                   -- PG사 결제에 사용되는 orderId
    ordered_at   TIMESTAMP    NOT NULL,                                   -- 주문일 (애플리케이션에서 생성)
    order_status VARCHAR(20) NOT NULL DEFAULT 'CREATED',  -- 주문 상태
    order_name   VARCHAR(20) NOT NULL
    total_amount INT         NOT NULL,                                   -- 총 주문 금액 (역정규화)
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    PRIMARY KEY (order_id)
);

-- 회원별 주문 조회
CREATE INDEX idx_orders_member_id
    ON orders (fk_member_id);

-- 관리자 주문 관리 (상태 + 기간)
CREATE INDEX idx_orders_status_ordered_at
    ON orders (order_status, ordered_at);

-- 4. order_item 테이블
CREATE TABLE order_item(
    order_item_id  BIGINT       NOT NULL AUTO_INCREMENT,      -- 주문 상품 ID (PK)
    fk_order_id    BIGINT       NOT NULL,
    fk_product_id  BIGINT       NOT NULL,                     -- 주문 ID (FK)-- 상품 ID (FK)
    product_name   VARCHAR(100) NOT NULL,
    order_price    INT          NOT NULL,
    order_quantity INT          NOT NULL,                     -- 주문 당시 상품명 (역정규화)-- 주문 당시 가격-- 주문 수량
    created_at     TIMESTAMP     NOT NULL,
    updated_at     TIMESTAMP     NOT NULL,

    PRIMARY KEY (order_item_id),
    --주문 ID와 상품 ID에 대한 유니크 제약 조건
    CONSTRAINT uq_fk_order_id_fk_product_id UNIQUE (fk_order_id, fk_product_id)
);

CREATE INDEX idx_order_item_order
    ON order_item (fk_order_id); -- 외래키 제약 조건 삭제 대신 인덱스 설정

CREATE INDEX idx_order_item_product -- 외래키 제약 조건 삭제 대신 인덱스 설정
    ON order_item (fk_product_id);

-- 5. pay 테이블
CREATE TABLE pay (
    pay_id BIGINT  NOT NULL AUTO_INCREMENT, -- 결제 ID (PK)
    fk_order_id BIGINT  NOT NULL,    -- 주문 ID (FK, Unique)
    order_id VARCHAR(255) NOT NULL, -- random으로 생성되는 주문 ID
    order_name VARCHAR(150) NOT NULL, -- 주문 이름 (snapshot)
    payment_key VARCHAR(255) NOT NULL, -- TOSS에서 만들어주는 Payment 식별 ID
    pay_method VARCHAR(50) NOT NULL, -- 결제 수단
    pay_amount INT NOT NULL,    -- 결제 금액
    pay_status  VARCHAR(20) NOT NULL,    -- 결제 상태
    requested_at TIMESTAMP NULL,     -- 결제가 일어난 시각
    approved_at TIMESTAMP NULL, -- 결제가 승인된 시각
    created_at TIMESTAMP NOT NULL, -- 레코드가 만들어진 시각
    updated_at TIMESTAMP NOT NULL, -- 레코드가 업데이트 된 시각

    PRIMARY KEY (pay_id),
    CONSTRAINT uq_pay_order_id UNIQUE (fk_order_id) -- 주문 하나당 결제는 하나
);

-- 6. delivery 테이블
CREATE TABLE delivery
(
    delivery_id     BIGINT       NOT NULL AUTO_INCREMENT, -- 배송 ID (PK)
    fk_order_id        BIGINT       NOT NULL,                -- 주문 ID (FK, Unique)
    delivery_status VARCHAR(20)  NOT NULL DEFAULT 'READY',-- 배송 상태
    tracking_no     VARCHAR(50) NULL,                     -- 운송장 번호
    ship_addr       VARCHAR(255) NOT NULL,                -- 배송지
    created_at      TIMESTAMP     NOT NULL,
    updated_at      TIMESTAMP     NOT NULL,

    PRIMARY KEY (delivery_id),
    CONSTRAINT uq_delivery_order_id UNIQUE (fk_order_id)           -- 주문 하나당 배송은 하나
);

