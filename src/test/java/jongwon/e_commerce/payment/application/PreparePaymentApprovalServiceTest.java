package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.MemberMemoryRepository;
import jongwon.e_commerce.order.application.OrderService;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderStatus;
import jongwon.e_commerce.order.dto.OrderItemRequest;
import jongwon.e_commerce.order.repository.OrderItemMemoryRepository;
import jongwon.e_commerce.order.repository.OrderMemoryRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.payment.repository.PaymentMemoryRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductMemoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PreparePaymentApprovalServiceTest {
    // 리포지토리
    OrderMemoryRepository orderMemoryRepository = new OrderMemoryRepository();
    OrderItemMemoryRepository orderItemMemoryRepository = new OrderItemMemoryRepository();
    ProductMemoryRepository productMemoryRepository = new ProductMemoryRepository();
    PaymentMemoryRepository paymentMemoryRepository = new PaymentMemoryRepository();

    // 서비스
    OrderService orderService = new OrderService(orderMemoryRepository,
            orderItemMemoryRepository, productMemoryRepository);
    PaymentCreateService paymentCreateService = new PaymentCreateService(paymentMemoryRepository,
            orderMemoryRepository);
    StockService stockService = new StockService(orderItemMemoryRepository,
            productMemoryRepository, orderMemoryRepository);
    PreparePaymentApprovalService preparePaymentApprovalService = new PreparePaymentApprovalService(stockService,
            paymentMemoryRepository, orderMemoryRepository);

    // 엔티티
    Product product1;
    Product product2;
    Order order;
    Pay pay;

    @BeforeEach
    public void beforeEach(){
        // 주문할 상품 저장
        product1 = productMemoryRepository.save("상품1", 1000);
        product1.changeStock(10);
        product1.startSelling();

        product2 = productMemoryRepository.save("상품2", 2000);
        product2.changeStock(10);
        product2.startSelling();

        // 주문할 상품과 수량 매핑
        List<OrderItemRequest> orderItemRequestList = List.of(
                new OrderItemRequest(product1.getProductId(), 2),
                new OrderItemRequest(product2.getProductId(), 3)
        );

        // 주문하기
        order = orderService.order(1L, "주문1", orderItemRequestList);

        // 결제 생성
        pay = paymentCreateService.preparePayment(order.getOrderId());
    }


    @AfterEach
    public void afterEach(){
        orderMemoryRepository.clearStore();
        orderItemMemoryRepository.clearStore();
        productMemoryRepository.clearStore();
        paymentMemoryRepository.clearStore();
    }

    @Test
    void 금액이_일치할_경우_정상적으로_처리된다(){
        // given
        long amountSendByClient = pay.getPayAmount();

        // when
        preparePaymentApprovalService.preparePaymentApproval(pay.getOrderId(), amountSendByClient);

        // then
        assertEquals(PayStatus.PENDING, pay.getPayStatus());
        assertEquals(OrderStatus.PAYMENT_PENDING, order.getOrderStatus());
        assertEquals(8, product1.getStockQuantity());
        assertEquals(7, product2.getStockQuantity());
    }

    @Test
    void 가격이_DB에_저장된_정보와_일치하지_않으면_실패한다(){
        //given
        long amountSendByClient = 5000L;

        // when && then
        assertThrows(InvalidAmountException.class,
                () -> preparePaymentApprovalService.preparePaymentApproval(pay.getOrderId(), amountSendByClient));
    }
}