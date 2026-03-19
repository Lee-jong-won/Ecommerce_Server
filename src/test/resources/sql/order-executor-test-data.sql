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
             'READY',
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