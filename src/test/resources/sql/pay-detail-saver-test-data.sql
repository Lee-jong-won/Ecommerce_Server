INSERT INTO member (
    member_id,
    login_id,
    password,
    member_name,
    email,
    addr
) VALUES (
             1,
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
             1,
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
             2,
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
             1,
             'test-id',
             1,
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
             1,
             1,
             1,
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
             2,
             1,
             2,
             'mobile',
             5000,
             1
         );

-- Pay
INSERT INTO pay (
    pay_id,
    fk_order_id,
    payment_id,
    pay_method,
    pay_amount,
    pay_status,
    approved_at
) VALUES (
          1,
          1,
          'paymentKey',
          'MOBILE',
          55000,
          'COMPLETE',
          TIMESTAMP '2023-01-01 10:00:00.123456'
          );