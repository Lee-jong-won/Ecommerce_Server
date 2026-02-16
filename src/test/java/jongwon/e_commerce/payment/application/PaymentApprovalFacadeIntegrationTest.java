package jongwon.e_commerce.payment.application;

import com.github.tomakehurst.wiremock.WireMockServer;
import jongwon.e_commerce.config.TestContainerConfig;
import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.jpa.MemberJpaRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.jpa.OrderItemJpaRepository;
import jongwon.e_commerce.order.repository.jpa.OrderJpaRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.external.toss.TossPaymentClient;
import jongwon.e_commerce.external.toss.TossPaymentClientErrorHandler;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.jpa.ProductJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import(TestContainerConfig.class)
@ActiveProfiles("test")
class PaymentApprovalFacadeIntegrationTest {

    @Autowired
    PaymentApprovalFacade paymentApprovalFacade;

    @Autowired
    TossPaymentClient tossPaymentClient;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    ProductJpaRepository productJpaRepository;

    @Autowired
    OrderJpaRepository orderJpaRepository;

    @Autowired
    OrderItemJpaRepository orderItemJpaRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    TossPaymentClientErrorHandler tossPaymentClientErrorHandler;

    WireMockServer wireMockServer;
    Member member;
    Product product;
    Order order;
    OrderItem orderItem;
    Pay payment;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();

        member = memberJpaRepository.save(Member.create("user", "1234",
                "Jongwon", "user@gmail.com", "경기도 고양시 덕양구"));

        product = productJpaRepository.save(Product.create("단팥빵", 2000));
        product.addStock(10);
        product.startSelling();
        productJpaRepository.save(product);

        order = orderJpaRepository.save(Order.createOrder(member.getMemberId(), "order1"));
        order.markPaymentPending();
        orderJpaRepository.save(order);

        orderItem = orderItemJpaRepository.save(OrderItem.createOrderItem(order.getOrderId(), product.getProductId(), product.getProductName(),
                product.getProductPrice(), 1));

        payment = paymentRepository.save(Pay.create(order.getOrderId(), order.getPayOrderId(),
                "paykey", 2000));
    }

    @AfterEach
    void cleanUp() {
        wireMockServer.stop();
        jdbcTemplate.queryForList(
                """
                SELECT TABLE_NAME
                FROM INFORMATION_SCHEMA.TABLES
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_TYPE = 'BASE TABLE'
                """,
                String.class
        ).forEach(table ->
                jdbcTemplate.execute("TRUNCATE TABLE " + table)
        );
    }







}
