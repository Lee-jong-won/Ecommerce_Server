package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.application.OrderService;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderStatus;
import jongwon.e_commerce.order.dto.OrderItemRequest;
import jongwon.e_commerce.order.repository.OrderItemMemoryRepository;
import jongwon.e_commerce.order.repository.OrderMemoryRepository;
import jongwon.e_commerce.payment.application.stub.StubExceptionPaymentResultService;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.exception.PaymentCompletionConsistencyException;
import jongwon.e_commerce.payment.repository.PaymentMemoryRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductMemoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class PaymentCompleteServiceTest {
    // 레포지토리
    OrderItemMemoryRepository orderItemMemoryRepository = new OrderItemMemoryRepository();
    ProductMemoryRepository productMemoryRepository = new ProductMemoryRepository();
    OrderMemoryRepository orderMemoryRepository = new OrderMemoryRepository();
    PaymentMemoryRepository paymentMemoryRepository = new PaymentMemoryRepository();

    // 서비스
    OrderService orderService = new OrderService(orderMemoryRepository, orderItemMemoryRepository, productMemoryRepository);
    StockService stockService = new StockService(orderItemMemoryRepository, productMemoryRepository, orderMemoryRepository);
    PaymentResultService paymentResultService;
    PaymentCompleteService paymentCompleteService;

    // 엔티티
    Product product1;
    Product product2;
    Order order;
    Pay pay;

    @BeforeEach
    void beforeEach(){
        product1 = productMemoryRepository.save("상품1", 1000);
        product1.changeStock(5);
        product1.startSelling();


        product2 = productMemoryRepository.save("상품2", 2000);
        product2.changeStock(5);
        product2.startSelling();

        order = orderService.order(1L, "주문1",
                List.of(
                        new OrderItemRequest(product1.getProductId(), 3),
                        new OrderItemRequest(product2.getProductId(), 2)
                ));
        pay = paymentMemoryRepository.save(order.getId(), order.getOrderId(), 7000L);
        pay.setPaymentId("payKey");
        pay.markPending();
        order.markPaymentPending();
    }

    @AfterEach
    void afterEach(){
        orderItemMemoryRepository.clearStore();
        productMemoryRepository.clearStore();
        orderMemoryRepository.clearStore();
        paymentMemoryRepository.clearStore();
    }

    @Test
    void 결제_성공후_주문성공이_DB에_반영된다(){
        // given
        paymentResultService = new PaymentResultService(paymentMemoryRepository, orderMemoryRepository);
        paymentCompleteService = new PaymentCompleteService(stockService, paymentResultService);
        TossPaymentApproveResponse tossPaymentApproveResponse = new TossPaymentApproveResponse(
                "카드", OffsetDateTime.parse("2024-02-13T03:18:14Z"), "DONE"
        );

        // when
        paymentCompleteService.completeSuccess(pay.getPaymentId(), pay.getOrderId(),
                tossPaymentApproveResponse.getApprovedAt(), tossPaymentApproveResponse.getMethod());

        // then
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
        assertEquals(PayStatus.SUCCESS, pay.getPayStatus());
        assertEquals(OffsetDateTime.parse("2024-02-13T03:18:14Z"), pay.getApprovedAt());
        assertEquals(PayMethod.CARD, pay.getPayMethod());
        assertEquals(2, product1.getStockQuantity());
        assertEquals(3, product2.getStockQuantity());
    }

    @Test
    void 결제_성공후_DB반영중_오류가_발생한_경우_예외가_발생한다(){
        // given
        paymentResultService = new StubExceptionPaymentResultService(paymentMemoryRepository, orderMemoryRepository);
        paymentCompleteService = new PaymentCompleteService(stockService, paymentResultService);
        TossPaymentApproveResponse tossPaymentApproveResponse = new TossPaymentApproveResponse(
                "카드", OffsetDateTime.parse("2024-02-13T03:18:14Z"), "DONE"
        );

        // when && then
        assertThrows(PaymentCompletionConsistencyException.class,
                () -> paymentCompleteService.completeSuccess(pay.getPaymentId(), pay.getOrderId(), tossPaymentApproveResponse.getApprovedAt(),
                        tossPaymentApproveResponse.getMethod()));
    }

    @Test
    void 결제_실패후_주문실패가_DB에_반영된다(){
        // given
        paymentResultService = new PaymentResultService(paymentMemoryRepository, orderMemoryRepository);
        paymentCompleteService = new PaymentCompleteService(stockService, paymentResultService);

        // when
        paymentCompleteService.completeFail(pay.getPaymentId(), pay.getOrderId());

        // then
        assertEquals(OrderStatus.FAILED, order.getOrderStatus());
        assertEquals(PayStatus.FAILED, pay.getPayStatus());
    }

    @Test
    void 결제_실패후_주문실패가_DB에_반영되지_않을시_예외가_발생한다(){
        // given
        paymentResultService = new StubExceptionPaymentResultService(paymentMemoryRepository, orderMemoryRepository);
        paymentCompleteService = new PaymentCompleteService(stockService, paymentResultService);

        // when && then
        assertThrows(PaymentCompletionConsistencyException.class,
                () -> paymentCompleteService.completeFail(pay.getPaymentId(), pay.getOrderId()));
    }

    @Test
    void 타임아웃_발생후_결제상태가_DB에_반영된다(){
        // given
        paymentResultService = new PaymentResultService(paymentMemoryRepository, orderMemoryRepository);
        paymentCompleteService = new PaymentCompleteService(stockService, paymentResultService);

        // when
        paymentCompleteService.completeTimeout(pay.getPaymentId(), pay.getOrderId());

        // then
        assertEquals(PayStatus.SYNC_TIMEOUT, pay.getPayStatus());
    }

    @Test
    void 타임아웃_발생후_결제상태가_DB에_반영되지_않을시_예외가_발생한다(){
        // given
        paymentResultService = new StubExceptionPaymentResultService(paymentMemoryRepository, orderMemoryRepository);
        paymentCompleteService = new PaymentCompleteService(stockService, paymentResultService);

        // when && then
        assertThrows(PaymentCompletionConsistencyException.class,
                () -> paymentCompleteService.completeTimeout(pay.getPaymentId(), pay.getOrderId()));
    }
}