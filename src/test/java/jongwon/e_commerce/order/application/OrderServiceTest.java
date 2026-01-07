package jongwon.e_commerce.order.application;

import jongwon.e_commerce.config.RepositoryTestConfig;
import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.infra.MemberRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.domain.OrderStatus;
import jongwon.e_commerce.order.domain.delivery.Delivery;
import jongwon.e_commerce.order.infra.DeliveryRepository;
import jongwon.e_commerce.order.infra.OrderItemRepository;
import jongwon.e_commerce.order.infra.OrderRepository;
import jongwon.e_commerce.order.presentation.dto.OrderItemRequest;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.infra.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({OrderService.class, RepositoryTestConfig.class})
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Test
    void 주문이_정상적으로_생성된다() {
        // given
        Member member = memberRepository.save(
                Member.create(
                        "loginId",
                        "password",
                        "홍길동",
                        "test@test.com",
                        "서울시 강남구"
                )
        );

        Product product1 = productRepository.save(
                Product.create("상품1", 10000)
        );
        product1.addStock(10);

        Product product2 = productRepository.save(
                Product.create("상품2", 20000)
        );
        product2.addStock(5);

        List<OrderItemRequest> requests = List.of(
                new OrderItemRequest(product1.getProductId(), 2),
                new OrderItemRequest(product2.getProductId(), 1)
        );

        // when
        Long orderId = orderService.order(member.getMemberId(), requests);

        // then

        // OrderItem 검증
        List<OrderItem> orderItems =
                orderItemRepository.findByOrderId(orderId);
        assertEquals(2, orderItems.size());

        // Delivery 검증
        Delivery delivery =
                deliveryRepository.findByOrderId(orderId)
                        .orElseThrow(() -> new RuntimeException("Delivery not found"));
        assertEquals(delivery.getShipAddress(), member.getAddr());

        // Order 검증
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AssertionError("Order not found"));
        assertEquals(member.getMemberId(), order.getMemberId());
        assertEquals(OrderStatus.CREATED, order.getOrderStatus());
        assertNotNull(order.getOrderedAt());
    }

}