package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.mock.stub.*;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.approve.PaymentApprovalService;
import jongwon.e_commerce.payment.application.approve.PaymentService;
import jongwon.e_commerce.payment.toss.DefaultPayApproveExceptionTranslator;
import jongwon.e_commerce.payment.application.approve.PayApprovalExecutor;
import jongwon.e_commerce.payment.application.approve.handler.PayOutcomeHandler;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.toss.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.repository.MPPayRepository;
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
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

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
    @Autowired
    MPPayRepository mpPayRepository;
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
        paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt, UUID.randomUUID().toString());

        // then
        Pay pay = paymentRepository.getByPaymentKey("paymentKey");
        MPPay mpPay = mpPayRepository.getByPay(pay);

        // then
        assertThat(pay.getPayMethod()).isEqualTo(PayMethod.MOBILE);
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.COMPLETE);

        assertThat(mpPay.getReceiptUrl()).isEqualTo("http://receipt.url");
        assertThat(mpPay.getCustomerMobilePhone()).isEqualTo("01012345678");
        assertThat(mpPay.getSettlementStatus()).isEqualTo("SETTLED");
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
        paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt, UUID.randomUUID().toString());

        // then
        Pay pay = paymentRepository.getByPaymentKey("paymentKey");
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.FAILED);
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
        paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt, UUID.randomUUID().toString());

        // then
        Pay pay = paymentRepository.getByPaymentKey("paymentKey");
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.TIME_OUT);
    }

    @Test
    void Connection_타임아웃은_아무처리도_하지_않는다(){
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
       paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt, UUID.randomUUID().toString());

        // then
        Pay pay = paymentRepository.getByPaymentKey("paymentKey");
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.PENDING);
    }

    @Test
    void ConnectionRequestTimeout은_아무처리도_하지_않는다(){
        // given
        initPaymentApprovalService(new StubPaymentRestApproveClientConnRequestTimeout());
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

    private void initPaymentApprovalService(PaymentApproveClient paymentApproveClient){
        payApprovalExecutor = payApprovalExecutor.builder().
                paymentApproveClient(paymentApproveClient).
                payApproveExceptionTranslator(new DefaultPayApproveExceptionTranslator(new ObjectMapper())).build();

        paymentApprovalService = PaymentApprovalService.builder().
                paymentService(paymentService).
                payApprovalExecutor(payApprovalExecutor).
                outcomeHandlers(outcomeHandlers).build();
    }
}
