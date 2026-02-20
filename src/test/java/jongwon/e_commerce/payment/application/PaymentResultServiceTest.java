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
    OrderMemoryRepository orderMemoryRepository = new OrderMemoryRepository();
    PaymentMemoryRepository paymentMemoryRepository = new PaymentMemoryRepository();
    PaymentResultService paymentResultService = new PaymentResultService(paymentMemoryRepository, orderMemoryRepository);

    // 엔티티
    Order order;
    Pay pay;

    @BeforeEach
    public void beforeEach(){
        // 주문하기
        order = orderMemoryRepository.save(1L, "주문1");

        // 결제 데이터 생성
        pay = paymentMemoryRepository.save(order.getId(), order.getOrderId(), 1000L);

        // 결제 승인 준비가 이루어졌다고 가정하고, 주문과 결제 상태 변경
        pay.markPending();
        order.markPaymentPending();
    }

    @AfterEach
    public void afterEach(){
        orderMemoryRepository.clearStore();
        paymentMemoryRepository.clearStore();
    }

    @Test
    void 외부_PG로부터_OK_응답시_결제정보와_주문정보가_성공으로_업데이트_된다(){
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