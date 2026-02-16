package jongwon.e_commerce.payment.application;


import jongwon.e_commerce.config.TestContainerConfig;
import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.jpa.MemberJpaRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.domain.OrderStatus;
import jongwon.e_commerce.order.repository.jpa.OrderItemJpaRepository;
import jongwon.e_commerce.order.repository.jpa.OrderJpaRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.repository.jpa.PaymentJpaRepository;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.jpa.ProductJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import java.time.OffsetDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Import(TestContainerConfig.class)
@ActiveProfiles("test")
class PaymentResultServiceIntegrationTest {

    @Autowired
    PaymentResultService paymentResultService;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    ProductJpaRepository productJpaRepository;

    @Autowired
    OrderJpaRepository orderJpaRepository;

    @Autowired
    OrderItemJpaRepository orderItemJpaRepository;

    @Autowired
    PaymentJpaRepository paymentJpaRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    Member member;
    Product product;
    Order order;
    OrderItem orderItem;
    Pay payment;

    @BeforeEach
    void setUp(){
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

        payment = paymentJpaRepository.save(Pay.create(order.getOrderId(), order.getPayOrderId(),
                "paykey", 2000));
    }

    @AfterEach
    void cleanUp(){
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

    @Test
    void 결제_성공시_주문_결제_재고가_정상적으로_반영된다() {
        // given
        TossPaymentApproveResponse response =
                new TossPaymentApproveResponse(
                        "CARD",
                        OffsetDateTime.now(),
                        OffsetDateTime.now()
                );

        // when
        paymentResultService.applySuccess(
                payment.getPaymentKey(),
                order.getPayOrderId(),
                response
        );

        // then
        Order updatedOrder = orderJpaRepository.findById(order.getOrderId()).orElseThrow();
        Pay updatedPayment = paymentJpaRepository.findById(payment.getPayId()).orElseThrow();
        Product updatedProduct = productJpaRepository.findById(product.getProductId()).orElseThrow();

        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderStatus.PAID);
        assertThat(updatedPayment.getPayStatus()).isEqualTo(PayStatus.SUCCESS);
        assertThat(updatedPayment.getPayMethod()).isEqualTo(PayMethod.CARD);
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(9);
    }

    @Test
    void 결제_실패시_주문과_결제_상태가_FAILED로_변경된다(){
        // when
        paymentResultService.applyFail(
                payment.getPaymentKey(),
                order.getPayOrderId()
        );

        // then
        Order updatedOrder = orderJpaRepository.findById(order.getOrderId()).orElseThrow();
        Pay updatedPayment = paymentJpaRepository.findById(payment.getPayId()).orElseThrow();

        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderStatus.FAILED);
        assertThat(updatedPayment.getPayStatus()).isEqualTo(PayStatus.FAILED);
    }

    @Test
    void 타임아웃시_결제상태는_타임아웃으로_변경된다() {
        // when
        paymentResultService.applyTimeout(payment.getPaymentKey());

        // then
        Pay updatedPayment = paymentJpaRepository.findById(payment.getPayId()).orElseThrow();
        assertThat(updatedPayment.getPayStatus()).isEqualTo(PayStatus.SYNC_TIMEOUT);
    }
}
