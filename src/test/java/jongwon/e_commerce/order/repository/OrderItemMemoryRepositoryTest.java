package jongwon.e_commerce.order.repository;

import jongwon.e_commerce.order.domain.OrderItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrderItemMemoryRepositoryTest {
    OrderItemMemoryRepository orderItemMemoryRepository = new OrderItemMemoryRepository();

    @AfterEach
    public void afterEach(){
        orderItemMemoryRepository.clearStore();
    }

    @Test
    void 주문상품이_정상적으로_저장된다(){
        // given
        Long orderId = 1L;
        Long productId = 1L;
        String productName = "상품1";
        int orderPrice = 10000;
        int orderQuantity = 10;

         // when
        OrderItem orderItem = orderItemMemoryRepository.save(orderId, productId, productName, orderPrice, orderQuantity);

        //then
        OrderItem result = orderItemMemoryRepository.findById(orderItem.getOrderItemId()).get();
        assertThat(result).isEqualTo(orderItem);
    }

    @Test
    void 주문상품이_정상적으로_조회된다(){
        // given
        orderItemMemoryRepository.save(1L, 2L, "상품1", 1000, 10);
        orderItemMemoryRepository.save(1L, 3L, "상품2", 2000, 1);

        // when
        List<OrderItem> findOrderItems = orderItemMemoryRepository.findOrderItems(1L);

        // then
        assertThat(findOrderItems).hasSize(2);
    }

}