package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.mock.fake.FakePaymentExecutor;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.approve.PaymentApprovalService;
import jongwon.e_commerce.payment.application.PaymentService;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.InsufficientBalance;
import jongwon.e_commerce.payment.domain.approve.outcome.none.ConnectionRequestTimeout;
import jongwon.e_commerce.payment.domain.approve.outcome.none.ConnectionTimeout;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayApproveSuccess;
import jongwon.e_commerce.payment.domain.approve.outcome.unknown.ReadTimeout;
import jongwon.e_commerce.payment.infrastructure.gateway.PaymentExecutor;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.application.approve.handler.PayOutcomeHandler;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.product.repository.ProductStockRepository;
import jongwon.e_commerce.support.scenario.FinishOrderData;
import jongwon.e_commerce.support.scenario.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

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
    ProductStockRepository productStockRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    PaymentRepository paymentRepository;
    PaymentApprovalService paymentApprovalService;

    @Test
    void 결제가_정상적으로_성공된다(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderItemRepository,
                orderRepository);
        Order order = finishOrderData.getOrder();
        List<OrderItem> orderItems = finishOrderData.getOrderItems();
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                order.getOrderId(), "TOSS", order.getTotalAmount());

        PayResult payResult = PayResult.builder()
                .payResultCommon(PayResult.PayResultCommon.builder()
                        .payMethod(PayMethod.MOBILE)
                        .amount(15000)
                        .approvedAt(OffsetDateTime.now())
                        .orderName("test-order")
                        .build())
                .paymentDetail(Map.of("phoneNumber", "010-1234-5678",
                        "settlementStatus", "DONE",
                        "receiptUrl", "https://naver.com"))
                .build();
        createPaymentService(new FakePaymentExecutor(new PayApproveSuccess(payResult)));

        // when
        paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt);

        // then
        Pay pay = paymentRepository.getByPaymentKey("paymentKey");

        assertThat(pay.getPayMethod()).isEqualTo(PayMethod.MOBILE);
        assertThat(pay.getPaymentDetail()).isNotNull();
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.COMPLETE);

        orderItems.forEach(item -> {
            Product product = productStockRepository.getById(item.getProduct().getProductId());
            assertThat(product.getStockQuantity())
                    .isEqualTo(item.getProduct().getStockQuantity() - item.getOrderQuantity());
        });
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
                "ORDER-DEFAULT", "TOSS", 15000);
        createPaymentService(new FakePaymentExecutor(new InsufficientBalance()));

        // when
        paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt);

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
                "ORDER-DEFAULT", "TOSS", 15000);
        createPaymentService(new FakePaymentExecutor(new ReadTimeout()));

        // when
        paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt);

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
                "ORDER-DEFAULT", "TOSS", 15000);
        createPaymentService(new FakePaymentExecutor(new ConnectionTimeout()));

        // when
        paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt);

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
                "ORDER-DEFAULT", "TOSS", 15000);
        createPaymentService(new FakePaymentExecutor(new ConnectionRequestTimeout()));

        // when
        paymentApprovalService.approvePayment(finishOrderData.getMember(), attempt);

        // then
        Pay pay = paymentRepository.getByPaymentKey("paymentKey");
        assertThat(pay.getPayStatus()).isEqualTo(PayStatus.PENDING);
    }

    void createPaymentService(PaymentExecutor paymentExecutor){
        paymentApprovalService = PaymentApprovalService.builder()
                .paymentService(paymentService)
                .outcomeHandlers(outcomeHandlers)
                .paymentExecutors(List.of(paymentExecutor))
                .build();
    }
}
