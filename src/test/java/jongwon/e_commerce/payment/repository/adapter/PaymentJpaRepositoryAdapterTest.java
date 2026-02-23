package jongwon.e_commerce.payment.repository.adapter;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.member.repository.adapter.MemberJpaRepositoryAdapter;
import jongwon.e_commerce.member.repository.jpa.MemberJpaRepository;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.order.repository.adapter.OrderJpaRepositoryAdapter;
import jongwon.e_commerce.order.repository.jpa.OrderJpaRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.payment.repository.jpa.PaymentJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Transactional
@ActiveProfiles("test")
class PaymentJpaRepositoryAdapterTest {

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    OrderJpaRepository orderJpaRepository;

    @Autowired
    PaymentJpaRepository paymentJpaRepository;

    MemberRepository memberRepository;
    OrderRepository orderRepository;
    PaymentRepository paymentRepository;

    @BeforeEach
    void beforeEach(){
        memberRepository = new MemberJpaRepositoryAdapter(memberJpaRepository);
        orderRepository = new OrderJpaRepositoryAdapter(orderJpaRepository);
        paymentRepository = new PaymentJpaRepositoryAdapter(paymentJpaRepository);
    }

    @Test
    void 정상적으로_핸드폰_결제_정보가_저장된다(){
        // given
        Member member = memberRepository.save("1234", "1234", "이종원",
                "dlwhddnjs951@naver.com",
                "경기도 고양시 덕양구");
        Order order = orderRepository.save(member.getMemberId(), "주문1");

        // when
        Pay pay = paymentRepository.save(order.getId(), "paymentKey", order.getOrderId(), 10000L);

        // then
        Pay result = paymentJpaRepository.findById(pay.getPayId()).get();
        assertEquals(pay.getPayId(), result.getPayId());
        assertEquals(pay.getPaymentId(), result.getPaymentId());
        assertEquals(pay.getOrderId(), result.getOrderId());
        assertEquals(pay.getFkOrderId(), result.getFkOrderId());
        assertEquals(pay.getPayAmount(), result.getPayAmount());
    }



}