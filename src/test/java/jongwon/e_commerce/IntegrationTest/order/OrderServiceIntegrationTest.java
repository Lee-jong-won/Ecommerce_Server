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
import jongwon.e_commerce.product.infra.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Import(TestContainerConfig.class)
@ActiveProfiles("test")
@Transactional
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

    @Test
    @DisplayName("주문 생성 시 Order와 OrderItem이 정상적으로 저장되고 총 금액이 계산된다")
    void order_success() {
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


}
