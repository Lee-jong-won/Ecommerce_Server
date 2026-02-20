package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.member.repository.MemberMemoryRepository;
import jongwon.e_commerce.order.application.OrderService;
import jongwon.e_commerce.order.repository.OrderItemMemoryRepository;
import jongwon.e_commerce.order.repository.OrderMemoryRepository;
import jongwon.e_commerce.payment.repository.PaymentMemoryRepository;
import jongwon.e_commerce.product.repository.ProductMemoryRepository;

import static org.junit.jupiter.api.Assertions.*;

class PaymentApprovalFacadeIntegrationTest {
    // 리포지토리
    MemberMemoryRepository memberMemoryRepository = new MemberMemoryRepository();
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

}