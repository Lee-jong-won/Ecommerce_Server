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
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.exception.PaymentCompletionConsistencyException;
import jongwon.e_commerce.payment.repository.PaymentMemoryRepository;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import jongwon.e_commerce.payment.toss.gateway.TossPaymentGateWay;
import jongwon.e_commerce.payment.toss.stub.StubClientExceptionTossPaymentGateWay;
import jongwon.e_commerce.payment.toss.stub.StubNormalTossPaymentGateWay;
import jongwon.e_commerce.payment.toss.stub.StubTimeoutExceptionTossPaymentGateWay;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductMemoryRepository;
import jongwon.e_commerce.product.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

class PaymentApprovalServiceTest {
    // 레포지토리
    OrderItemRepository orderItemRepository = new OrderItemMemoryRepository();
    ProductRepository productRepository = new ProductMemoryRepository();
    OrderRepository orderRepository = new OrderMemoryRepository();
    PaymentRepository paymentRepository = new PaymentMemoryRepository();

    // 서비스
    OrderService orderService = new OrderService(orderRepository, orderItemRepository, productRepository);
    StockChanger stockChanger = new StockChangerImpl(orderItemRepository, productRepository, orderRepository);
    PaymentApprovalPreprocessor paymentApprovalPreprocessor = new PaymentApprovalPreprocessorImpl(paymentRepository, orderRepository);
    PaymentResultUpdater paymentResultUpdater;
    PaymentCompleter paymentCompleter;
    TossPaymentGateWay tossPaymentGateWay;
    PaymentApprovalService paymentApprovalService;

    // 엔티티
    Product product1;
    Product product2;
    Order order;
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
    }

    @AfterEach
    void afterEach(){
        orderItemRepository.clearStore();
        orderRepository.clearStore();
        productRepository.clearStore();
        paymentRepository.clearStore();
    }

    @Test
    void 결제_성공_응답을_받은_후_성공적으로_DB에_반영된다(){
        // given
        paymentResultUpdater = new PaymentResultUpdaterImpl(paymentRepository, orderRepository);
        paymentCompleter = new PaymentCompleterImpl(stockChanger, paymentResultUpdater);
        tossPaymentGateWay = new StubNormalTossPaymentGateWay(null, null);
        paymentApprovalService = new PaymentApprovalService(paymentApprovalPreprocessor, tossPaymentGateWay, paymentCompleter);
        TossPaymentApproveRequest request = new TossPaymentApproveRequest(order.getOrderId(), "paymentId", order.getTotalAmount());

        // when
        paymentApprovalService.approvePayment(request);

        // then
        Pay pay = paymentRepository.findByOrderId(order.getOrderId()).get();
        assertEquals(PayStatus.SUCCESS, pay.getPayStatus());
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
        assertEquals(2, product1.getStockQuantity());
        assertEquals(3, product2.getStockQuantity());
    }

    @Test
    void 결제_성공_응답을_받은후_DB에_반영_실패시_예외가_발생한다(){
        // given
        paymentResultUpdater = new StubExceptionPaymentResultUpdaterImpl();
        paymentCompleter = new PaymentCompleterImpl(stockChanger, paymentResultUpdater);
        tossPaymentGateWay = new StubNormalTossPaymentGateWay(null, null);
        paymentApprovalService = new PaymentApprovalService(paymentApprovalPreprocessor, tossPaymentGateWay, paymentCompleter);
        TossPaymentApproveRequest request = new TossPaymentApproveRequest(order.getOrderId(), "paymentId", order.getTotalAmount());

        // when && then
        assertThrows(PaymentCompletionConsistencyException.class,
                () -> paymentApprovalService.approvePayment(request));
    }

    @Test
    void 결제_실패_응답을_받은후_성공적으로_DB에_반영된다(){
        // given
        paymentResultUpdater = new PaymentResultUpdaterImpl(paymentRepository, orderRepository);
        paymentCompleter = new PaymentCompleterImpl(stockChanger, paymentResultUpdater);
        tossPaymentGateWay = new StubClientExceptionTossPaymentGateWay(null, null);
        paymentApprovalService = new PaymentApprovalService(paymentApprovalPreprocessor, tossPaymentGateWay, paymentCompleter);
        TossPaymentApproveRequest request = new TossPaymentApproveRequest(order.getOrderId(), "paymentId", order.getTotalAmount());

        // when
        paymentApprovalService.approvePayment(request);

        // then
        Pay pay = paymentRepository.findByOrderId(order.getOrderId()).get();
        assertEquals(OrderStatus.FAILED, order.getOrderStatus());
        assertEquals(PayStatus.FAILED, pay.getPayStatus());
    }

    @Test
    void 결제_실패_응답을_받은후_DB에_반영_실패시_예외가_발생한다(){
        // given
        paymentResultUpdater = new StubExceptionPaymentResultUpdaterImpl();
        paymentCompleter = new PaymentCompleterImpl(stockChanger, paymentResultUpdater);
        tossPaymentGateWay = new StubClientExceptionTossPaymentGateWay(null, null);
        paymentApprovalService = new PaymentApprovalService(paymentApprovalPreprocessor, tossPaymentGateWay, paymentCompleter);
        TossPaymentApproveRequest request = new TossPaymentApproveRequest(order.getOrderId(), "paymentId", order.getTotalAmount());

        // when && then
        assertThrows(PaymentCompletionConsistencyException.class,
                () -> paymentApprovalService.approvePayment(request));
    }

    @Test
    void 타임아웃_발생후_DB에_정상적으로_반영된다(){
        // given
        paymentResultUpdater = new PaymentResultUpdaterImpl(paymentRepository, orderRepository);
        paymentCompleter = new PaymentCompleterImpl(stockChanger, paymentResultUpdater);
        tossPaymentGateWay = new StubTimeoutExceptionTossPaymentGateWay(null, null);
        paymentApprovalService = new PaymentApprovalService(paymentApprovalPreprocessor, tossPaymentGateWay, paymentCompleter);
        TossPaymentApproveRequest request = new TossPaymentApproveRequest(order.getOrderId(), "paymentId", order.getTotalAmount());

        // when
        paymentApprovalService.approvePayment(request);

        // then
        Pay pay = paymentRepository.findByOrderId(order.getOrderId()).get();
        assertEquals(PayStatus.SYNC_TIMEOUT, pay.getPayStatus());
        assertEquals(OrderStatus.PAYMENT_PENDING, order.getOrderStatus());
    }

    @Test
    void 타임아웃_발생후_DB에_반영_실패시_예외가_발생한다(){
        // given
        paymentResultUpdater = new StubExceptionPaymentResultUpdaterImpl();
        paymentCompleter = new PaymentCompleterImpl(stockChanger, paymentResultUpdater);
        tossPaymentGateWay = new StubTimeoutExceptionTossPaymentGateWay(null, null);
        paymentApprovalService = new PaymentApprovalService(paymentApprovalPreprocessor, tossPaymentGateWay, paymentCompleter);

        TossPaymentApproveRequest request = new TossPaymentApproveRequest(order.getOrderId(), "paymentId", order.getTotalAmount());

        // when && then
        assertThrows(PaymentCompletionConsistencyException.class,
                () -> paymentApprovalService.approvePayment(request));
    }

}