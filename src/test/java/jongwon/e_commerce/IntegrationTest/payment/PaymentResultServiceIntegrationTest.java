package jongwon.e_commerce.IntegrationTest.payment;


import jongwon.e_commerce.IntegrationTest.config.TestContainerConfig;
import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.infra.MemberRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.domain.OrderStatus;
import jongwon.e_commerce.order.infra.OrderItemRepository;
import jongwon.e_commerce.order.infra.OrderRepository;
import jongwon.e_commerce.payment.application.PaymentResultService;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.infra.PaymentRepository;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.infra.ProductRepository;
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
    MemberRepository memberRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    Member member;
    Product product;
    Order order;
    OrderItem orderItem;
    Pay payment;

    @BeforeEach
    void setUp(){
        member = memberRepository.save(Member.create("user", "1234",
                "Jongwon", "user@gmail.com", "경기도 고양시 덕양구"));

        product = productRepository.save(Product.create("단팥빵", 2000));
        product.addStock(10);
        product.startSelling();
        productRepository.save(product);

        order = orderRepository.save(Order.createOrder(member.getMemberId(), "order1"));
        order.markPaymentPending();
        orderRepository.save(order);

        orderItem = orderItemRepository.save(OrderItem.createOrderItem(order.getOrderId(), product.getProductId(), product.getProductName(),
                product.getProductPrice(), 1));

        payment = paymentRepository.save(Pay.create(order.getOrderId(), order.getPayOrderId(),
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
        Order updatedOrder = orderRepository.findById(order.getOrderId()).orElseThrow();
        Pay updatedPayment = paymentRepository.findById(payment.getPayId()).orElseThrow();
        Product updatedProduct = productRepository.findById(product.getProductId()).orElseThrow();

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
        Order updatedOrder = orderRepository.findById(order.getOrderId()).orElseThrow();
        Pay updatedPayment = paymentRepository.findById(payment.getPayId()).orElseThrow();

        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderStatus.FAILED);
        assertThat(updatedPayment.getPayStatus()).isEqualTo(PayStatus.FAILED);
    }

    @Test
    void 타임아웃시_결제상태는_타임아웃으로_변경된다() {
        // when
        paymentResultService.applyTimeout(payment.getPaymentKey());

        // then
        Pay updatedPayment = paymentRepository.findById(payment.getPayId()).orElseThrow();
        assertThat(updatedPayment.getPayStatus()).isEqualTo(PayStatus.SYNC_TIMEOUT);
    }
}
