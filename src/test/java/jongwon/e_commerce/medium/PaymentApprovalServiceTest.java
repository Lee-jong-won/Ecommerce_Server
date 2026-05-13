package jongwon.e_commerce.medium;

import jongwon.e_commerce.member.repository.MemberRepository;
import jongwon.e_commerce.mock.stub.StubPaymentExecutor;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderItem;
import jongwon.e_commerce.order.domain.OrderStatus;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.payment.application.PayProcessStateManager;
import jongwon.e_commerce.payment.application.approve.PaySuccessProcessor;
import jongwon.e_commerce.payment.domain.PayRequest;
import jongwon.e_commerce.payment.exception.*;
import jongwon.e_commerce.payment.repository.PayRequestRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.approve.PaymentApprovalService;
import jongwon.e_commerce.payment.application.approve.PayPreprocessor;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.infrastructure.gateway.PaymentExecutor;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PaymentApprovalServiceTest {
    @Autowired
    PayPreprocessor payPreprocessor;
    @Autowired
    PayProcessStateManager payProcessStateManager;
    @Autowired
    PaySuccessProcessor paySuccessProcessor;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductStockRepository productStockRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    PayRequestRepository payRequestRepository;
    PaymentApprovalService paymentApprovalService;

    @Test
    void 결제성공(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderRepository);

        Order order = finishOrderData.getOrder();
        List<OrderItem> orderItems = order.getOrderItems();

        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                order.getOrderId(), "TOSS", order.getTotalAmount());

        OffsetDateTime approvedAt = OffsetDateTime.now();
        createPaymentService(StubPaymentExecutor.success(PayResult.builder()
                .payResultCommon(PayResult.PayResultCommon.builder()
                        .payMethod(PayMethod.MOBILE)
                        .amount(order.getTotalAmount())
                        .approvedAt(approvedAt)
                        .orderName(order.getOrderName())
                        .build())
                .paymentDetail(Map.of("phoneNumber", "010-1234-5678",
                        "settlementStatus", "DONE",
                        "receiptUrl", "https://naver.com"))
                .build()));

        // when
        paymentApprovalService.approvePayment(attempt);

        // then
        PayRequest payRequest = payRequestRepository.getByPaymentKey("paymentKey");
        assertThat(payRequest.getPayStatus()).isEqualTo(PayStatus.COMPLETE);

        Order updatedOrder = payRequest.getOrder();
        assertThat(updatedOrder.getOrderStatus()).isEqualTo(OrderStatus.PAID);

        Pay pay = paymentRepository.getByPayRequestId(payRequest.getId());
        assertThat(pay.getPayAmount()).isEqualTo(order.getTotalAmount());
        assertThat(pay.getPayMethod()).isEqualTo(PayMethod.MOBILE);
        assertThat(pay.getApprovedAt()).isEqualTo(approvedAt);
        assertThat(pay.getPaymentDetail()).isNotNull();
        assertThat(pay.getPayRequest().getId()).isEqualTo(payRequest.getId());

        orderItems.forEach(item -> {
            Product product = productStockRepository.getById(item.getProduct().getProductId());
            assertThat(product.getStockQuantity())
                    .isEqualTo(item.getProduct().getStockQuantity() - item.getOrderQuantity());
        });
    }

    @Test
    void 결제_실패(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", "TOSS", 15000);
        createPaymentService(StubPaymentExecutor.failure(new PayClientException(PayErrorCode.INVALID_CARD)));

        // when && then
        assertThrows(PayClientException.class,
                () -> paymentApprovalService.approvePayment(attempt));
        PayRequest payRequest = payRequestRepository.getByPaymentKey("paymentKey");
        assertThat(payRequest.getPayStatus()).isEqualTo(PayStatus.BUSINESS_FAILED);
    }

    @Test
    void Read_타임아웃(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", "TOSS", 15000);
        createPaymentService(StubPaymentExecutor.failure(new PayTimeoutException()));

        // when && then
        assertThrows(PayUnknownOutcomeException.class,
                () -> paymentApprovalService.approvePayment(attempt));
        PayRequest payRequest = payRequestRepository.getByPaymentKey("paymentKey");
        assertThat(payRequest.getPayStatus()).isEqualTo(PayStatus.UNKNOWN);
    }

    @Test
    void Connection_타임아웃(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderRepository);
        Order order = finishOrderData.getOrder();

        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                order.getOrderId(), "TOSS", order.getTotalAmount());
        createPaymentService(StubPaymentExecutor.failure(new PayServerException("연결 타임아웃이 발생했습니다")));

        // when && then
        assertThrows(PayServerException.class,
                () -> paymentApprovalService.approvePayment(attempt));
        PayRequest payRequest = payRequestRepository.getByPaymentKey("paymentKey");
        assertThat(payRequest.getPayStatus()).isEqualTo(PayStatus.SERVER_FAILED);
    }

    @Test
    void ConnectionRequestTimeout(){
        // given
        FinishOrderData finishOrderData = TestDataFactory.finishOrder(
                memberRepository,
                productRepository,
                orderRepository);
        PayApproveAttempt attempt = new PayApproveAttempt("paymentKey",
                "ORDER-DEFAULT", "TOSS", 15000);
        createPaymentService(StubPaymentExecutor.failure(new PayServerException("커넥션 풀이 고갈되었습니다")));

        // when && then
        assertThrows(PayServerException.class,
                () -> paymentApprovalService.approvePayment(attempt));
        PayRequest payRequest = payRequestRepository.getByPaymentKey("paymentKey");
        assertThat(payRequest.getPayStatus()).isEqualTo(PayStatus.SERVER_FAILED);
    }

    void createPaymentService(PaymentExecutor paymentExecutor){
        paymentApprovalService = new PaymentApprovalService(payPreprocessor,
                payProcessStateManager, List.of(paymentExecutor), paySuccessProcessor);
    }
}
