package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.PGCaller;
import jongwon.e_commerce.payment.application.approve.PaymentApprovalService;
import jongwon.e_commerce.payment.application.PaymentService;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayApproveSuccess;
import jongwon.e_commerce.payment.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.application.approve.handler.PayOutcomeHandler;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.FinishOrderData;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@Transactional
public class PaymentApprovalServiceTest {
    @Autowired
    PaymentService paymentService;
    @Autowired
    List<PayOutcomeHandler> outcomeHandlers;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @MockitoBean
    PGCaller pgCaller;
    @Autowired
    PaymentApprovalService paymentApprovalService;
    @Test
    void 결제가_정상적으로_성공된다(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", 15000);

        when(pgCaller.processPayApprove(any(), any())).
                thenReturn(new PayApproveSuccess(PayResult.builder().build()));

        // when
        paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt, "TOSS");

        // then
        Pay pay = paymentRepository.getByPaymentKey("paymentKey");

        // then
        assertThat(pay.getPayMethod()).isEqualTo(PayMethod.MOBILE);
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.COMPLETE);
    }

    @Test
    void 결제_실패가_성공적으로_처리된다(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", 15000);

        // when
        paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt, UUID.randomUUID().toString());

        // then
        Pay pay = paymentRepository.getByPaymentKey("paymentKey");
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.FAILED);
    }

    @Test
    void Read_타임아웃이_성공적으로_처리된다(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", 15000);

        // when
        paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt, UUID.randomUUID().toString());

        // then
        Pay pay = paymentRepository.getByPaymentKey("paymentKey");
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.TIME_OUT);
    }

    @Test
    void Connection_타임아웃은_아무처리도_하지_않는다(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", 15000);

        // when
       paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt, UUID.randomUUID().toString());

        // then
        Pay pay = paymentRepository.getByPaymentKey("paymentKey");
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.PENDING);
    }

    @Test
    void ConnectionRequestTimeout은_아무처리도_하지_않는다(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", 15000);

        // when
        paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt, UUID.randomUUID().toString());

        // then
        Pay pay = paymentRepository.getByPaymentKey("paymentKey");
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.PENDING);
    }
}
