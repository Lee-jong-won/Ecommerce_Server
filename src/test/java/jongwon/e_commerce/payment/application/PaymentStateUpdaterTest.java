package jongwon.e_commerce.payment.application;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderStatus;
import jongwon.e_commerce.order.repository.OrderMemoryRepository;
import jongwon.e_commerce.order.repository.OrderRepository;
import jongwon.e_commerce.payment.application.approve.process3.common.PaymentStateUpdater;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.repository.memory.PaymentMemoryRepository;
import jongwon.e_commerce.payment.repository.PaymentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentStateUpdaterTest {
    OrderRepository orderRepository = new OrderMemoryRepository();
    PaymentRepository paymentRepository = new PaymentMemoryRepository();
    PaymentStateUpdater paymentStateUpdater = new PaymentStateUpdater(paymentRepository, orderRepository);

    @AfterEach
    public void afterEach(){
        orderRepository.clearStore();
        paymentRepository.clearStore();
    }

    @Test
    void 외부_PG로부터_OK_응답시_결제정보와_주문정보가_성공으로_업데이트_된다(){
        // given
        Order order = orderRepository.save(1L, "주문1");
        Pay pay = paymentRepository.save(order.getId(), "paymentId", order.getOrderId(), 1000L);
        order.markPaymentPending();

        // when
        paymentStateUpdater.applySuccess(pay.getOrderId(), OffsetDateTime.parse("2024-02-13T03:18:14Z"), "카드");

        // then
        assertEquals(OrderStatus.PAID, order.getOrderStatus());
        assertEquals(PayStatus.SUCCESS, pay.getPayStatus());
        assertEquals(PayMethod.CARD, pay.getPayMethod());
        assertEquals(OffsetDateTime.parse("2024-02-13T03:18:14Z"), pay.getApprovedAt());
    }

    @Test
    void 외부_PG로부터_에러_응답시_결제상태가_실패로_반영된다(){
        // given
        Order order = orderRepository.save(1L, "주문1");
        Pay pay = paymentRepository.save(order.getId(), "paymentId", order.getOrderId(), 1000L);
        order.markPaymentPending();

        // when
        paymentStateUpdater.applyFail(pay.getOrderId());

        // then
        assertEquals(PayStatus.FAILED, pay.getPayStatus());
        assertEquals(OrderStatus.FAILED, order.getOrderStatus());
    }

    @Test
    void 타임아웃_시_타임아웃_상태로_반영된다(){
        // given
        Order order = orderRepository.save(1L, "주문1");
        Pay pay = paymentRepository.save(order.getId(), "paymentId", order.getOrderId(), 1000L);
        order.markPaymentPending();

        // when
        paymentStateUpdater.applyTimeout(pay.getOrderId());

        // then
        assertEquals(PayStatus.SYNC_TIMEOUT, pay.getPayStatus());
    }

}