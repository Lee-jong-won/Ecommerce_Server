package jongwon.e_commerce.payment.application.approve;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.mock.fake.*;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayRequest;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.payment.repository.PayRequestRepository;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.FinishOrderData;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PayPreprocessorTest {
    PayPreprocessor payPreprocessor;
    PayRequestRepository payRequestRepository;
    MemberRepository memberRepository;
    ProductRepository productRepository;
    OrderItemRepository orderItemRepository;
    OrderRepository orderRepository;

    @BeforeEach
    void init(){
        memberRepository = new FakeMemberRepository();
        orderRepository = new FakeOrderRepository();
        productRepository = new FakeProductRepository();
        orderItemRepository = new FakeOrderItemRepository();
        payRequestRepository = new FakePayRequestRepository();
        payPreprocessor = new PayPreprocessor(orderRepository, payRequestRepository);
    }

    @Test
    void 주문_검증이_정상적으로_완료된후_결제가_정상적으로_저장된다(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(memberRepository, productRepository, orderItemRepository, orderRepository);
        Order order = finishOrderData.getOrder();

        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                order.getOrderId(),
                "TOSS", order.getTotalAmount());

        // when
        PayRequest pay = payPreprocessor.preProcess(finishOrderData.getMember(), request);

        // then
        assertThat(pay.getPaymentKey()).isEqualTo("a4CWyWY5m89PNh7xJwhk1");
        assertThat(pay.getPayAmount()).isEqualTo(finishOrderData.getOrder().getTotalAmount());
        assertThat(pay.getOrder()).isNotNull();
        assertThat(pay.getId()).isNotNull();
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.PENDING);
    }

    @Test
    void 주문_금액이_일치하지_못하면_예외가_발생한다(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(memberRepository, productRepository, orderItemRepository, orderRepository);
        Order order = finishOrderData.getOrder();

        PayApproveAttempt request = new PayApproveAttempt("a4CWyWY5m89PNh7xJwhk1",
                order.getOrderId(),
                "TOSS", order.getTotalAmount() - 5000);

        // when && then
        assertThrows(InvalidAmountException.class, () -> payPreprocessor.preProcess(finishOrderData.getMember(), request));
    }

    @Test
    void 소유자가_일치하지_않으면_예외가_발생한다(){

    }
}