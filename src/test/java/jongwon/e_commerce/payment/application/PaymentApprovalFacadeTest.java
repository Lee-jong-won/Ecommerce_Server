package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.application.OrderService;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderStatus;
import jongwon.e_commerce.order.dto.OrderItemRequest;
import jongwon.e_commerce.order.repository.OrderItemMemoryRepository;
import jongwon.e_commerce.order.repository.OrderMemoryRepository;
import jongwon.e_commerce.payment.application.stub.StubExceptionPaymentResultService;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.exception.PaymentCompletionConsistencyException;
import jongwon.e_commerce.payment.exception.external.TossPaymentServerException;
import jongwon.e_commerce.payment.exception.external.TossPaymentTimeoutException;
import jongwon.e_commerce.payment.repository.PaymentMemoryRepository;
import jongwon.e_commerce.payment.toss.TossPaymentGateWay;
import jongwon.e_commerce.payment.toss.stub.StubClientExceptionTossPaymentGateWay;
import jongwon.e_commerce.payment.toss.stub.StubNormalTossPaymentGateWay;
import jongwon.e_commerce.payment.toss.stub.StubTimeoutExceptionTossPaymentGateWay;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductMemoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PaymentApprovalFacadeTest {

    // 레포지토리
    OrderItemMemoryRepository orderItemMemoryRepository = new OrderItemMemoryRepository();
    ProductMemoryRepository productMemoryRepository = new ProductMemoryRepository();
    OrderMemoryRepository orderMemoryRepository = new OrderMemoryRepository();
    PaymentMemoryRepository paymentMemoryRepository = new PaymentMemoryRepository();

    // 서비스
    OrderService orderService = new OrderService(orderMemoryRepository, orderItemMemoryRepository, productMemoryRepository);
    StockService stockService = new StockService(orderItemMemoryRepository, productMemoryRepository, orderMemoryRepository);
    PaymentCreateService paymentCreateService = new PaymentCreateService(paymentMemoryRepository, orderMemoryRepository);
    PreparePaymentApprovalService preparePaymentApprovalService = new PreparePaymentApprovalService(paymentMemoryRepository, orderMemoryRepository);
    PaymentResultService paymentResultService;
    PaymentCompleteService paymentCompleteService;
    TossPaymentGateWay tossPaymentGateWay;
    PaymentApprovalFacade paymentApprovalFacade;

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

        pay = paymentCreateService.preparePayment(order.getOrderId());
    }

    @AfterEach
    void afterEach(){
        orderItemMemoryRepository.clearStore();
        orderMemoryRepository.clearStore();
        productMemoryRepository.clearStore();
        paymentMemoryRepository.clearStore();
    }

    @Test
    void 결제_성공_응답을_받은_후_성공적으로_DB에_반영된다(){
        // given
        paymentResultService = new PaymentResultService(paymentMemoryRepository, orderMemoryRepository);
        paymentCompleteService = new PaymentCompleteService(stockService, paymentResultService);
        tossPaymentGateWay = new StubNormalTossPaymentGateWay(null, null);
        paymentApprovalFacade = new PaymentApprovalFacade(preparePaymentApprovalService, tossPaymentGateWay, paymentCompleteService);
        TossPaymentApproveRequest request = new TossPaymentApproveRequest(pay.getOrderId(), pay.getPaymentId(), pay.getPayAmount());

        // when
        paymentApprovalFacade.approvePayment(request);

        // then
        assertEquals(PayStatus.SUCCESS, pay.getPayStatus());
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
        assertEquals(2, product1.getStockQuantity());
        assertEquals(3, product2.getStockQuantity());
    }

    @Test
    void 결제_성공_응답을_받은후_DB에_반영_실패시_예외가_발생한다(){
        // given
        paymentResultService = new StubExceptionPaymentResultService(paymentMemoryRepository, orderMemoryRepository);
        paymentCompleteService = new PaymentCompleteService(stockService, paymentResultService);
        tossPaymentGateWay = new StubNormalTossPaymentGateWay(null, null);
        paymentApprovalFacade = new PaymentApprovalFacade(preparePaymentApprovalService, tossPaymentGateWay, paymentCompleteService);
        TossPaymentApproveRequest request = new TossPaymentApproveRequest(pay.getOrderId(), pay.getPaymentId(), pay.getPayAmount());

        // when && then
        assertThrows(PaymentCompletionConsistencyException.class,
                () -> paymentApprovalFacade.approvePayment(request));
    }

    @Test
    void 결제_실패_응답을_받은후_성공적으로_DB에_반영된다(){
        // given
        paymentResultService = new PaymentResultService(paymentMemoryRepository, orderMemoryRepository);
        paymentCompleteService = new PaymentCompleteService(stockService, paymentResultService);
        tossPaymentGateWay = new StubClientExceptionTossPaymentGateWay(null, null);
        paymentApprovalFacade = new PaymentApprovalFacade(preparePaymentApprovalService, tossPaymentGateWay, paymentCompleteService);
        TossPaymentApproveRequest request = new TossPaymentApproveRequest(pay.getOrderId(), pay.getPaymentId(), pay.getPayAmount());

        // when
        paymentApprovalFacade.approvePayment(request);

        // then
        assertEquals(OrderStatus.FAILED, order.getOrderStatus());
        assertEquals(PayStatus.FAILED, pay.getPayStatus());
    }

    @Test
    void 결제_실패_응답을_받은후_DB에_반영_실패시_예외가_발생한다(){
        // given
        paymentResultService = new StubExceptionPaymentResultService(paymentMemoryRepository, orderMemoryRepository);
        paymentCompleteService = new PaymentCompleteService(stockService, paymentResultService);
        tossPaymentGateWay = new StubClientExceptionTossPaymentGateWay(null, null);
        paymentApprovalFacade = new PaymentApprovalFacade(preparePaymentApprovalService, tossPaymentGateWay, paymentCompleteService);
        TossPaymentApproveRequest request = new TossPaymentApproveRequest(pay.getOrderId(), pay.getPaymentId(), pay.getPayAmount());

        // when && then
        assertThrows(PaymentCompletionConsistencyException.class,
                () -> paymentApprovalFacade.approvePayment(request));
    }

    @Test
    void 타임아웃_발생후_DB에_정상적으로_반영된다(){
        // given
        paymentResultService = new PaymentResultService(paymentMemoryRepository, orderMemoryRepository);
        paymentCompleteService = new PaymentCompleteService(stockService, paymentResultService);
        tossPaymentGateWay = new StubTimeoutExceptionTossPaymentGateWay(null, null);
        paymentApprovalFacade = new PaymentApprovalFacade(preparePaymentApprovalService, tossPaymentGateWay, paymentCompleteService);
        TossPaymentApproveRequest request = new TossPaymentApproveRequest(pay.getOrderId(), pay.getPaymentId(), pay.getPayAmount());

        // when
        paymentApprovalFacade.approvePayment(request);

        // then
        assertEquals(PayStatus.SYNC_TIMEOUT, pay.getPayStatus());
        assertEquals(OrderStatus.PAYMENT_PENDING, order.getOrderStatus());
    }

}