package jongwon.e_commerce.order.repository;

import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.infra.OrderItemJpaRepository;
import jongwon.e_commerce.order.infra.OrderItemJpaRepositoryAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class OrderItemRepositoryTest {

    @Autowired
    OrderItemJpaRepository orderItemJpaRepository;

    @Test
    void 주문_상품이_정상적으로_저장된다_JPA(){
        //given
        OrderItemRepository orderItemRepository = new OrderItemJpaRepositoryAdapter(orderItemJpaRepository);
        Long orderId = 1L;
        Long productId = 100L;
        String productName = "상품1";
        int orderPrice = 10000;
        int orderQuantity = 4;

        //when
        OrderItem orderItem = orderItemRepository.save(orderId, productId, productName, orderPrice,
                orderQuantity);

        //then
        assertEquals(productId, orderItem.getProductId());
        assertEquals(orderId, orderItem.getOrderId());
        assertEquals(productName, orderItem.getProductName());
        assertEquals(orderPrice, orderItem.getOrderPrice());
        assertEquals(orderQuantity, orderItem.getOrderQuantity());
    }



}