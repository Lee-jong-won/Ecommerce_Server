package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.application.OrderService;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderStatus;
import jongwon.e_commerce.order.dto.OrderItemRequest;
import jongwon.e_commerce.order.repository.OrderItemMemoryRepository;
import jongwon.e_commerce.order.repository.OrderItemRepository;
import jongwon.e_commerce.order.repository.OrderMemoryRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.stub.StubExceptionPaymentResultUpdaterImpl;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.exception.PaymentCompletionConsistencyException;
import jongwon.e_commerce.payment.repository.PaymentMemoryRepository;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductMemoryRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentCompleterImplTest {
    // 레포지토리
    OrderItemRepository orderItemRepository = new OrderItemMemoryRepository();
    ProductRepository productRepository = new ProductMemoryRepository();
    OrderRepository orderRepository = new OrderMemoryRepository();
    PaymentRepository paymentRepository = new PaymentMemoryRepository();

    // 서비스
    OrderService orderService = new OrderService(orderRepository, orderItemRepository, productRepository);
    StockChanger stockChanger = new StockChangerImpl(orderItemRepository, productRepository, orderRepository);
    PaymentResultUpdater paymentResultUpdater;
    PaymentCompleterImpl paymentCompleter;

    // 엔티티
    Product product1;
    Product product2;
    Order order;
    Pay pay;

    @BeforeEach
    void beforeEach(){
        product1 = productRepository.save("상품1", 1000);
        product1.changeStock(5);
        product1.startSelling();


        product2 = productRepository.save("상품2", 2000);
        product2.changeStock(5);
        product2.startSelling();

        order = orderService.order(1L, "주문1",
                List.of(
                        new OrderItemRequest(product1.getProductId(), 3),
                        new OrderItemRequest(product2.getProductId(), 2)
                ));

        pay = paymentRepository.save(order.getId(), "paymentId", order.getOrderId(), 7000L);
        order.markPaymentPending();
    }

    @AfterEach
    void afterEach(){
        orderItemRepository.clearStore();
        productRepository.clearStore();
        orderRepository.clearStore();
        paymentRepository.clearStore();
    }

    @Test
    void 결제_성공후_주문성공이_DB에_반영된다(){
        // given
        paymentResultUpdater = new PaymentResultUpdaterImpl(paymentRepository, orderRepository);
        paymentCompleter = new PaymentCompleterImpl(stockChanger, paymentResultUpdater);
        TossPaymentApproveResponse tossPaymentApproveResponse = new TossPaymentApproveResponse(
                "카드", OffsetDateTime.parse("2024-02-13T03:18:14Z"), "DONE"
        );

        // when
        paymentCompleter.completeSuccess(pay.getPaymentId(), pay.getOrderId(),
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
        paymentResultUpdater = new StubExceptionPaymentResultUpdaterImpl();
        paymentCompleter = new PaymentCompleterImpl(stockChanger, paymentResultUpdater);
        TossPaymentApproveResponse tossPaymentApproveResponse = new TossPaymentApproveResponse(
                "카드", OffsetDateTime.parse("2024-02-13T03:18:14Z"), "DONE"
        );

        // when && then
        assertThrows(PaymentCompletionConsistencyException.class,
                () -> paymentCompleter.completeSuccess(pay.getPaymentId(), pay.getOrderId(), tossPaymentApproveResponse.getApprovedAt(),
                        tossPaymentApproveResponse.getMethod()));
    }

    @Test
    void 결제_실패후_주문실패가_DB에_반영된다(){
        // given
        paymentResultUpdater = new PaymentResultUpdaterImpl(paymentRepository, orderRepository);
        paymentCompleter = new PaymentCompleterImpl(stockChanger, paymentResultUpdater);

        // when
        paymentCompleter.completeFail(pay.getPaymentId(), pay.getOrderId());

        // then
        assertEquals(OrderStatus.FAILED, order.getOrderStatus());
        assertEquals(PayStatus.FAILED, pay.getPayStatus());
    }

    @Test
    void 결제_실패후_주문실패가_DB에_반영되지_않을시_예외가_발생한다(){
        // given
        paymentResultUpdater = new StubExceptionPaymentResultUpdaterImpl();
        paymentCompleter = new PaymentCompleterImpl(stockChanger, paymentResultUpdater);

        // when && then
        assertThrows(PaymentCompletionConsistencyException.class,
                () -> paymentCompleter.completeFail(pay.getPaymentId(), pay.getOrderId()));
    }

    @Test
    void 타임아웃_발생후_결제상태가_DB에_반영된다(){
        // given
        paymentResultUpdater = new PaymentResultUpdaterImpl(paymentRepository, orderRepository);
        paymentCompleter = new PaymentCompleterImpl(stockChanger, paymentResultUpdater);

        // when
        paymentCompleter.completeTimeout(pay.getPaymentId(), pay.getOrderId());

        // then
        assertEquals(PayStatus.SYNC_TIMEOUT, pay.getPayStatus());
    }

    @Test
    void 타임아웃_발생후_결제상태가_DB에_반영되지_않을시_예외가_발생한다(){
        // given
        paymentResultUpdater = new StubExceptionPaymentResultUpdaterImpl();
        paymentCompleter = new PaymentCompleterImpl(stockChanger, paymentResultUpdater);

        // when && then
        assertThrows(PaymentCompletionConsistencyException.class,
                () -> paymentCompleter.completeTimeout(pay.getPaymentId(), pay.getOrderId()));
    }
}