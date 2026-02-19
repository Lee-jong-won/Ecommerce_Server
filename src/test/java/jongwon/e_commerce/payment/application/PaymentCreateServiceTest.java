package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.member.domain.Member;
import jongwon.e_commerce.member.repository.MemberMemoryRepository;
import jongwon.e_commerce.order.application.OrderService;
import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.dto.OrderItemRequest;
import jongwon.e_commerce.order.repository.OrderItemMemoryRepository;
import jongwon.e_commerce.order.repository.OrderMemoryRepository;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.exception.OrderNotExistException;
import jongwon.e_commerce.payment.repository.PaymentMemoryRepository;
import jongwon.e_commerce.product.domain.Product;
import jongwon.e_commerce.product.repository.ProductMemoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentCreateServiceTest {
    // 레포지토리
    MemberMemoryRepository memberMemoryRepository = new MemberMemoryRepository();
    ProductMemoryRepository productMemoryRepository = new ProductMemoryRepository();
    OrderMemoryRepository orderMemoryRepository = new OrderMemoryRepository();
    OrderItemMemoryRepository orderItemMemoryRepository = new OrderItemMemoryRepository();
    PaymentMemoryRepository paymentMemoryRepository = new PaymentMemoryRepository();

    // 서비스
    OrderService orderService = new OrderService(orderMemoryRepository, orderItemMemoryRepository, productMemoryRepository);
    PaymentCreateService paymentCreateService = new PaymentCreateService(paymentMemoryRepository, orderMemoryRepository);

    @AfterEach
    public void afterEach(){
        memberMemoryRepository.clearStore();
        productMemoryRepository.clearStore();
        orderMemoryRepository.clearStore();
        orderItemMemoryRepository.clearStore();
        paymentMemoryRepository.clearStore();
    }

    @Test
    void Pay_객체가_정상적으로_생성된다(){
        // given
        Member member = memberMemoryRepository.save("wwwl7749", "1234", "이종원",
                "dlwhddnjs951@naver.com", "경기도 고양시 덕양구");
        Product product1 = productMemoryRepository.save("상품1", 1000);
        Product product2 = productMemoryRepository.save("상품2", 2000);

        List<OrderItemRequest> orderItemRequestList = List.of(
                new OrderItemRequest(product1.getProductId(), 2),
                new OrderItemRequest(product2.getProductId(), 3)
        );
        Order order = orderService.order(member.getMemberId(), "주문1", orderItemRequestList);

        // when
        Pay pay = paymentCreateService.preparePayment(order.getOrderId());

        // then
        assertEquals(order.getTotalAmount(), pay.getPayAmount());
        assertEquals(order.getId(), pay.getFkOrderId());
        assertEquals(order.getOrderId(), pay.getOrderId());
        assertEquals(PayStatus.CREATED, pay.getPayStatus());
    }

    @Test
    void 존재하지_않는_주문에_대한_Pay_객체는_생성될_수_없다(){
        //when && then
        assertThrows(OrderNotExistException.class,
                () -> paymentCreateService.preparePayment("1234"));
    }
}