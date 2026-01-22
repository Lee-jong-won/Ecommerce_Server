package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.infra.OrderItemRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
import jongwon.e_commerce.product.infra.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @InjectMocks
    StockService stockService;
    @Mock
    OrderItemRepository orderItemRepository;
    @Mock
    ProductRepository productRepository;

    @Test
    void 주문에_포함된_모든_상품의_재고가_차감된다() {
        // given
        Long orderId = 1L;

        OrderItem item1 = mock(OrderItem.class);
        OrderItem item2 = mock(OrderItem.class);

        when(item1.getProductId()).thenReturn(10L);
        when(item1.getOrderQuantity()).thenReturn(2);

        when(item2.getProductId()).thenReturn(20L);
        when(item2.getOrderQuantity()).thenReturn(3);

        Product product1 = mock(Product.class);
        Product product2 = mock(Product.class);

        when(orderItemRepository.findByOrderId(orderId))
                .thenReturn(List.of(item1, item2));
        when(productRepository.findById(10L))
                .thenReturn(Optional.of(product1));
        when(productRepository.findById(20L))
                .thenReturn(Optional.of(product2));

        // when
        stockService.decreaseStock(orderId);

        // then
        verify(product1).removeStock(2);
        verify(product2).removeStock(3);
    }

    @Test
    void 주문상품이_없으면_아무_상품도_차감하지_않는다() {
        // given
        Long orderId = 1L;

        when(orderItemRepository.findByOrderId(orderId))
                .thenReturn(List.of());

        // when
        stockService.decreaseStock(orderId);

        // then
        verifyNoInteractions(productRepository);
    }

    @Test
    void 상품이_존재하지_않으면_ProductNotFoundException이_발생한다() {
        // given
        Long orderId = 1L;

        OrderItem orderItem = mock(OrderItem.class);
        when(orderItem.getProductId()).thenReturn(10L);

        when(orderItemRepository.findByOrderId(orderId))
                .thenReturn(List.of(orderItem));

        when(productRepository.findById(10L))
                .thenReturn(Optional.empty());

        // when & then
        assertThrowsExactly(
                ProductNotFoundException.class,
                () -> stockService.decreaseStock(orderId)
        );
    }

}