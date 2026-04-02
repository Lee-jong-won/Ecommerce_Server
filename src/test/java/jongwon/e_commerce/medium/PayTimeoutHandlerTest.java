package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.approve.handler.PayTimeoutHandler;
import jongwon.e_commerce.payment.controller.dto.PayFailureResponse;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveTimeout;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PayTimeoutHandlerTest {

    @Autowired
    PayTimeoutHandler payTimeoutHandler;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void 결제에_타임아웃이_성공적으로_반영된다(){
        // given
        Pay pay = TestDataFactory.finishPayPreProcess(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository,
                paymentRepository
        );
        PayApproveTimeout payApproveTimeout = new PayApproveTimeout();

        // when
        PayFailureResponse payFailureResponse = (PayFailureResponse) payTimeoutHandler.handle(pay, payApproveTimeout);

        // then
        assertThat(payFailureResponse.getPayStatus()).isEqualTo(PayStatus.TIME_OUT);
        assertThat(payFailureResponse.getCode()).isEqualTo("PAYMENT_TIMEOUT");
        assertThat(payFailureResponse.getMessage()).isEqualTo("결제 시도가 많습니다. 다시 시도해주세요");
    }


}