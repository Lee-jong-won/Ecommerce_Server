package jongwon.e_commerce.order.repository;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.impl.OrderMemoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMemoryRepositoryTest {

    OrderMemoryRepository orderMemoryRepository = new OrderMemoryRepository();

    @AfterEach
    void afterEach(){
        orderMemoryRepository.clearStore();
    }

    @Test
    void 주문이_정상적으로_저장된다(){
        // given
        Long memberId = 1L;
        String orderId = "주문1";

        // when
        Order order = orderMemoryRepository.save(memberId, orderId);

        // then
        Order result = orderMemoryRepository.findById(order.getId()).get();
        assertThat(result).isEqualTo(order);
    }

    @Test
    void 주문이_주문번호로_조회된다(){
        // given
        Order order1 = orderMemoryRepository.save(1L, "order1");
        orderMemoryRepository.save(1L, "order2");

        // when
        Order result = orderMemoryRepository.findByOrderId(order1.getOrderId()).get();

        // then
        assertThat(result).isEqualTo(order1);
    }

}