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
    OrderMemoryRepository orderMemoryRepository = new OrderMemoryRepository();
    PaymentMemoryRepository paymentMemoryRepository = new PaymentMemoryRepository();
    PaymentCreateService paymentCreateService = new PaymentCreateService(paymentMemoryRepository, orderMemoryRepository);

    @AfterEach
    public void afterEach(){
        orderMemoryRepository.clearStore();
        paymentMemoryRepository.clearStore();
    }

    @Test
    void Pay_객체가_정상적으로_생성된다(){
        // given
        Order order = orderMemoryRepository.save(1L, "order1");
        order.setTotalAmount(10000);

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