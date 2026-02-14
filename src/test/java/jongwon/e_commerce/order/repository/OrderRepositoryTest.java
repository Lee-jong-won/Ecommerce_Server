package jongwon.e_commerce.order.repository;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.infra.OrderJpaRepository;
import jongwon.e_commerce.order.infra.OrderJpaRepositoryAdapter;
import jongwon.e_commerce.product.domain.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    OrderJpaRepository orderJpaRepository;

    @Test
    void 주문이_정상적으로_저장된다_JPA(){
        // given
        Long memberId = 1L;
        String orderName = "목도리 외 1건";
        OrderRepository orderRepository = new OrderJpaRepositoryAdapter(orderJpaRepository);

        // when
        Order order = orderRepository.save(memberId, orderName);

        // then
        assertEquals(memberId, order.getMemberId());
        assertEquals(orderName, order.getOrderName());
    }

    @Test
    void 주문이_정상적으로_조회된다_JPA(){
        // given
        Long memberId = 1L;
        String orderName = "목도리 외 1건";
        OrderRepository orderRepository = new OrderJpaRepositoryAdapter(orderJpaRepository);

        // when
        Order saveOrder = orderRepository.save(memberId, orderName);
        Order findOrder = orderRepository.findById(saveOrder.getOrderId());

        // then
        assertEquals(saveOrder.getOrderId(), findOrder.getOrderId());
    }


}