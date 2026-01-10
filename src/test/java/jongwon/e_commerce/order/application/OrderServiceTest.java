package jongwon.e_commerce.order.application;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.infra.MemberRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.infra.DeliveryRepository;
import jongwon.e_commerce.order.infra.OrderItemRepository;
import jongwon.e_commerce.order.infra.OrderRepository;
import jongwon.e_commerce.order.presentation.dto.OrderItemRequest;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.domain.ProductStatus;
import jongwon.e_commerce.product.exception.NotEnoughStockException;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
import jongwon.e_commerce.product.infra.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private DeliveryRepository deliveryRepository;
    @Mock
    private OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("주문 중 존재하지 않는 상품이 있으면 예외가 발생한다")
    void order_productNotFound_throwException() {
        // given
        Long memberId = 1L;
        Long productId = 100L;

        OrderItemRequest request =
                new OrderItemRequest(productId, 2);

        List<OrderItemRequest> requests = List.of(request);

        Member member = Member.create("wwwl7749", "1234",
                "회원", "dlwhddnjs951@naver.com","서울");
        Order order = Order.createOrder(memberId);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        when(productRepository.findById(productId))
                .thenReturn(Optional.empty()); // 핵심

        // when & then
        assertThrows(
                ProductNotFoundException.class,
                () -> orderService.order(memberId, requests)
        );
    }

    @Test
    @DisplayName("주문 수량이 재고보다 많으면 예외가 발생한다")
    void order_notEnoughStock_throwException() {
        // given
        Long memberId = 1L;
        Long productId = 100L;

        OrderItemRequest request =
                new OrderItemRequest(productId, 10);

        List<OrderItemRequest> requests = List.of(request);

        Member member = Member.create("wwwl7749", "1234",
                "회원", "dlwhddnjs951@naver.com","서울");
        Order order = Order.createOrder(memberId);

        Product product = Product.create("신발", 10000);
        product.changeStock(5);
        product.changeStatus(ProductStatus.SELLING);

        when(memberRepository.findById(memberId))
                .thenReturn(Optional.of(member));

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        when(productRepository.findById(productId))
                .thenReturn(Optional.of(product));

        // when & then
        assertThrows(
                NotEnoughStockException.class,
                () -> orderService.order(memberId, requests)
        );
    }
}