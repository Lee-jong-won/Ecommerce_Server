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
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.repository.PaymentMemoryRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductMemoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentResultServiceTest {

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
    PaymentResultService paymentResultService = new PaymentResultService(paymentMemoryRepository, orderMemoryRepository);

    // 엔티티
    Member member;
    Product product1;
    Product product2;
    Order order;
    Pay pay;

    @BeforeEach
    public void beforeEach(){
        // 주문할 멤버 설정
        member = memberMemoryRepository.save("wwwl7749", "1234", "이종원",
                "dlwhddnjs951@naver.com", "경기도 고양시 덕양구");

        // 주문할 상품 설정
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
        order = orderService.order(member.getMemberId(), "주문1", orderItemRequestList);

        // 결제 데이터 생성
        pay = paymentCreateService.preparePayment(order.getOrderId());

        // 결제 승인 준비
        preparePaymentApprovalService.preparePaymentApproval(pay.getOrderId(), pay.getPayAmount());
    }

    @AfterEach
    public void afterEach(){
        memberMemoryRepository.clearStore();
        productMemoryRepository.clearStore();
        orderMemoryRepository.clearStore();
        orderItemMemoryRepository.clearStore();
        paymentMemoryRepository.clearStore();
    }

    @Test
    void 외부_PG로부터_OK_응답시_결제정보와_주문정보가_성공으로_업데이트_된다(){
        // given
        TossPaymentApproveResponse tossPaymentApproveResponse = new TossPaymentApproveResponse("카드",
                OffsetDateTime.parse("2024-02-13T03:18:14Z"), "DONE");

        // when
        paymentResultService.applySuccess(pay.getOrderId(), OffsetDateTime.parse("2024-02-13T03:18:14Z"), "카드");

        // then
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
        assertEquals(PayStatus.SUCCESS, pay.getPayStatus());
        assertEquals(PayMethod.CARD, pay.getPayMethod());
        assertEquals(OffsetDateTime.parse("2024-02-13T03:18:14Z"), pay.getApprovedAt());
    }

    @Test
    void 외부_PG로부터_에러_응답시_결제상태가_실패로_반영된다(){

        // when
        paymentResultService.applyFail(pay.getOrderId());

        // then
        assertEquals(PayStatus.FAILED, pay.getPayStatus());
        assertEquals(OrderStatus.FAILED, order.getOrderStatus());
    }

    @Test
    void 타임아웃_시_타임아웃_상태로_반영된다(){
        // when
        paymentResultService.applyTimeout(pay.getOrderId());

        // then
        assertEquals(PayStatus.SYNC_TIMEOUT, pay.getPayStatus());
    }


}