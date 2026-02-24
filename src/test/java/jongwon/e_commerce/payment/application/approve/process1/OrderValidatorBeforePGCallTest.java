package jongwon.e_commerce.payment.application.approve.process1;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.order.domain.OrderStatus;
import jongwon.e_commerce.order.repository.impl.OrderMemoryRepository;
import jongwon.e_commerce.payment.application.approve.validator.OrderValidatorBeforePGCall;
import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayStatus;
import jongwon.e_commerce.payment.exception.InvalidAmountException;
import jongwon.e_commerce.payment.repository.impl.PaymentMemoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderValidatorBeforePGCallTest {
    // 리포지토리
    OrderMemoryRepository orderMemoryRepository = new OrderMemoryRepository();
    PaymentMemoryRepository paymentMemoryRepository = new PaymentMemoryRepository();
    OrderValidatorBeforePGCall orderValidatorBeforePGCall = new OrderValidatorBeforePGCall(paymentMemoryRepository, orderMemoryRepository);

    @AfterEach
    public void afterEach(){
        orderMemoryRepository.clearStore();
        paymentMemoryRepository.clearStore();
    }

    @Test
    void 금액이_일치할_경우_정상적으로_처리된다(){
        // given
        Order order = orderMemoryRepository.save(1L, "주문1");
        long amountSendByClient = order.getTotalAmount();
        String paymentIdSendByClient = "paymentId";

        // when
        Pay pay = orderValidatorBeforePGCall.preparePaymentApproval(paymentIdSendByClient, order.getOrderId(), amountSendByClient);

        // then
        assertEquals(PayStatus.PENDING, pay.getPayStatus());
        assertEquals(OrderStatus.PAYMENT_PENDING, order.getOrderStatus());
    }

    @Test
    void 가격이_DB에_저장된_정보와_일치하지_않으면_실패한다(){
        //given
        Order order = orderMemoryRepository.save(1L, "주문1");
        long amountSendByClient = 5000L;
        String paymentIdSendByClient = "paymentId";

        // when && then
        assertThrows(InvalidAmountException.class,
                () -> orderValidatorBeforePGCall.preparePaymentApproval(paymentIdSendByClient, order.getOrderId(), amountSendByClient));
    }
}