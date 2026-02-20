package jongwon.e_commerce.order.repository.adapter;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.jpa.MemberJpaRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderStatus;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.order.repository.jpa.OrderJpaRepository;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
class OrderJpaRepositoryAdapterTest {

    @Autowired
    OrderJpaRepository orderJpaRepository;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    void 주문이_정상적으로_저장된다_JPA(){
        // given
        OrderRepository orderRepository = new OrderJpaRepositoryAdapter(orderJpaRepository);

        Member member = Member.create("1234", "1234", "이종원",
                "dlwhddnjs951@naver.com", "경기도 고양시 덕양구");
        memberJpaRepository.save(member);

        // when
        Order orderEntity = orderRepository.save(member.getMemberId(), "주문1");
        Order findOrderEntity = orderJpaRepository.findById(orderEntity.getId()).orElseThrow();

        // then
        assertEquals("주문1", findOrderEntity.getOrderName());
        assertEquals(OrderStatus.CREATED, findOrderEntity.getOrderStatus());
        assertEquals(member.getMemberId(), findOrderEntity.getMemberId());
    }

    @Test
    void 주문이_정상적으로_조회된다_JPA(){
        // given
        OrderRepository orderRepository = new OrderJpaRepositoryAdapter(orderJpaRepository);

        Member member = memberJpaRepository.save(Member.create("1234", "1234", "이종원",
                "dlwhddnjs951@naver.com", "경기도 고양시 덕양구"));

        memberJpaRepository.save(member);
        Order order = Order.createOrder(member.getMemberId(), "주문1");

        // when
        orderJpaRepository.save(order);

        // then
        assertDoesNotThrow(() -> orderRepository.findById(order.getId()));
    }

    @Test
    void 존재하지_않는_주문_조회시_예외_throw_JPA(){
        //given
        OrderRepository orderRepository = new OrderJpaRepositoryAdapter(orderJpaRepository);

        //when && then
        assertEquals(true, orderRepository.findById(1L).isEmpty());
    }

}