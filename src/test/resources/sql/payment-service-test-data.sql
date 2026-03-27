INSERT INTO member (
    member_id,
    login_id,
    password,
    member_name,
    email,
    addr
) VALUES (
             6,
             'testUser',
             'password123',
             'gildong',
             'test@example.com',
             'seoul'
         );

INSERT INTO product (
    product_id,
    product_name,
    product_price,
    product_status,
    stock_quantity
) VALUES (
             9,
             'labtob',
             50000,
             'SELLING',
             10
         );

INSERT INTO product (
    product_id,
    product_name,
    product_price,
    product_status,
    stock_quantity
) VALUES (
             10,
             'mobile',
             5000,
             'SELLING',
             10
         );

INSERT INTO orders (
    id,
    order_id,
    fk_member_id,
    order_name,
    order_status,
    total_amount,
    ordered_at
) VALUES (
             4,
             'test-id',
             6,
             '노트북 외 1건',
             'ORDERED',
             55000,
             CURRENT_TIMESTAMP
         );

-- order_item 1
INSERT INTO order_item (
    order_item_id,
    fk_order_id,
    fk_product_id,
    product_name,
    order_price,
    order_quantity
) VALUES (
             3,
             4,
             9,
             'labtob',
             50000,
             1
         );

-- order_item 2
INSERT INTO order_item (
    order_item_id,
    fk_order_id,
    fk_product_id,
    product_name,
    order_price,
    order_quantity
) VALUES (
             4,
             4,
             10,
             'mobile',
             5000,
             1
         );