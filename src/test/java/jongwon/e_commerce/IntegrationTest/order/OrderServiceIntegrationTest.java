package jongwon.e_commerce.IntegrationTest.order;

import jongwon.e_commerce.IntegrationTest.config.TestContainerConfig;
import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.infra.MemberRepository;
import jongwon.e_commerce.order.application.OrderService;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.infra.OrderItemRepository;
import jongwon.e_commerce.order.infra.OrderRepository;
import jongwon.e_commerce.order.presentation.dto.OrderItemRequest;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
import jongwon.e_commerce.product.infra.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Import(TestContainerConfig.class)
@ActiveProfiles("test")
class OrderServiceIntegrationTest {

    @Autowired
    OrderService orderService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    Member member;
    Product product1;
    Product product2;

    @BeforeEach
    void setUp(){
        member = memberRepository.save(Member.create("user", "1234",
                "Jongwon", "user@gmail.com", "경기도 고양시 덕양구"));
        product1 = productRepository.save(Product.create("단팥빵", 2000));
        product2 = productRepository.save(Product.create("크림빵", 3000));
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
    void 주문_성공() {
        // given
        List<OrderItemRequest> requests = List.of(
                new OrderItemRequest(product1.getProductId(), 2),
                new OrderItemRequest(product2.getProductId(), 1)
        );

        // when
        Long orderId = orderService.order(member.getMemberId(), "order1", requests);

        // then
        Order order = orderRepository.findById(orderId).orElseThrow();
        List<OrderItem> orderItems =
                orderItemRepository.findByOrderId(orderId);

        assertThat(orderItems).hasSize(2);
        assertThat(order.getTotalAmount()).isEqualTo(7000);
        assertThat(orderItems)
                .extracting(OrderItem::getProductName)
                .containsExactlyInAnyOrder("단팥빵", "크림빵");
    }

    @Test
    void 주문_중_상품이_없으면_전체_롤백된다() {
        List<OrderItemRequest> requests = List.of(
                new OrderItemRequest(9999L, 1) // 존재하지 않는 상품
        );

        // when
        assertThatThrownBy(() ->
                orderService.order(
                        member.getMemberId(),
                        "실패 주문",
                        requests
                )
        ).isInstanceOf(ProductNotFoundException.class);

        // then
        assertThat(orderRepository.count()).isZero();
        assertThat(orderItemRepository.count()).isZero();
    }


}
