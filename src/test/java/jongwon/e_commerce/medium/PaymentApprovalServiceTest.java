package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.mock.stub.StubPaymentRestApproveClientConnTimeout;
import jongwon.e_commerce.mock.stub.StubPaymentRestApproveClientErrorResponse;
import jongwon.e_commerce.mock.stub.StubPaymentRestApproveClientNormal;
import jongwon.e_commerce.mock.stub.StubPaymentRestApproveClientReadTimeout;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.approve.PaymentApprovalService;
import jongwon.e_commerce.payment.application.approve.PaymentService;
import jongwon.e_commerce.payment.application.approve.external.DefaultPayApproveExceptionTranslator;
import jongwon.e_commerce.payment.application.approve.external.PayApprovalExecutor;
import jongwon.e_commerce.payment.application.approve.handler.PayOutcomeHandler;
import jongwon.e_commerce.payment.controller.dto.PayFailureResponse;
import jongwon.e_commerce.payment.controller.dto.PaySuccessResponse;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.payment.toss.PaymentApproveClient;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.support.scenario.FinishOrderData;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
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
    PaymentApprovalService paymentApprovalService;
    PayApprovalExecutor payApprovalExecutor;

    @Test
    void 결제가_정상적으로_성공된다(){
        // given
        initPaymentApprovalService(new StubPaymentRestApproveClientNormal());
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", 15000);

        // when
        PaySuccessResponse response = (PaySuccessResponse) paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt);

        // then
        assertThat(response.getPayMethod()).isEqualTo(PayMethod.MOBILE);
        assertThat(response.getPayStatus()).isEqualTo(PayStatus.COMPLETE);
        assertThat(response.getPayAmount()).isEqualTo(15000);
    }

    @Test
    void 결제_실패가_성공적으로_처리된다(){
        // given
        initPaymentApprovalService(new StubPaymentRestApproveClientErrorResponse());
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", 15000);

        // when
        PayFailureResponse response = (PayFailureResponse) paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt);

        // then
        assertThat(response.getCode()).isEqualTo("FAILED_INTERNAL_SYSTEM_PROCESSING");
        assertThat(response.getMessage()).isEqualTo("내부 시스템 처리 작업이 실패했습니다. 잠시 후 다시 시도해주세요.");
    }

    @Test
    void Read_타임아웃이_성공적으로_처리된다(){
        // given
        initPaymentApprovalService(new StubPaymentRestApproveClientReadTimeout());
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", 15000);

        // when
        PayFailureResponse payFailureResponse = (PayFailureResponse) paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt);

        // then
        assertThat(payFailureResponse.getPayStatus()).isEqualTo(PayStatus.TIME_OUT);
        assertThat(payFailureResponse.getCode()).isEqualTo("PAYMENT_TIMEOUT");
        assertThat(payFailureResponse.getMessage()).isEqualTo("결제 시도가 많습니다. 다시 시도해주세요");
    }

    @Test
    void Connection_타임아웃이_성공적으로_처리된다(){
        // given
        initPaymentApprovalService(new StubPaymentRestApproveClientConnTimeout());
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", 15000);

        // when
        PayFailureResponse payFailureResponse = (PayFailureResponse) paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt);

        // then
        assertThat(payFailureResponse.getPayStatus()).isEqualTo(PayStatus.PENDING);
        assertThat(payFailureResponse.getCode()).isEqualTo("CONNECTION_TIMEOUT");
        assertThat(payFailureResponse.getMessage()).isEqualTo("일시적인 네트워크 오류가 발생했습니다. 다시 시도해주세요");
    }

    private void initPaymentApprovalService(PaymentApproveClient paymentApproveClient){
        payApprovalExecutor = payApprovalExecutor.builder().
                paymentApproveClient(paymentApproveClient).
                payApproveExceptionTranslator(new DefaultPayApproveExceptionTranslator()).build();

        paymentApprovalService = PaymentApprovalService.builder().
                paymentService(paymentService).
                payApprovalExecutor(payApprovalExecutor).
                outcomeHandlers(outcomeHandlers).build();
    }
}
