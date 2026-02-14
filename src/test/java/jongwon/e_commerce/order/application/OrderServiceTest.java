package jongwon.e_commerce.order.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.presentation.dto.OrderItemRequest;
import jongwon.e_commerce.product.exception.ProductNotFoundException;
import jongwon.e_commerce.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest("classes = {OrderService.class}")
@ActiveProfiles("test")
public class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Autowired
    ProductRepository productRepository;

    @Test
    void 주문이_정상적으로_완료된다() {
        //given
        Long memberId = 1L;
        List<OrderItemRequest> orderItemRequestList = List.of(
                new OrderItemRequest(1L, 3), new OrderItemRequest(2L, 4),
                new OrderItemRequest(3L, 4)
        );

        //when
        productRepository.save("상품1", 3000);
        productRepository.save("상품2", 2000);
        productRepository.save("상품3", 4000);
        Order order = orderService.order(memberId, "주문1", orderItemRequestList);

        //then
        assertEquals(33000, order.getTotalAmount());
    }

    @Test
    void 존재하지_않는_상품은_주문할_수_없다() {
        //given
        Long memberId = 1L;
        List<OrderItemRequest> orderItemRequestList = List.of(
                new OrderItemRequest(1L, 3), new OrderItemRequest(2L, 4),
                new OrderItemRequest(3L, 4)
        );

        //when
        productRepository.save("상품1", 3000);
        productRepository.save("상품2", 2000);

        //then
        assertThrows(ProductNotFoundException.class,
                () -> orderService.order(memberId, "주문1", orderItemRequestList));
    }
    
    
}
