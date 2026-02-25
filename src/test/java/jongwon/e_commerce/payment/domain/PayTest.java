package jongwon.e_commerce.payment.domain;

import jongwon.e_commerce.order.domain.Order;
import jongwon.e_commerce.payment.application.approve.result.context.MPPayDetail;
import jongwon.e_commerce.payment.application.approve.result.context.PaymentContext;
import jongwon.e_commerce.payment.application.approve.result.context.PaymentDetail;
import jongwon.e_commerce.payment.exception.UnsupportedPayMethodException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PayTest {
    @Test
    void 정상적으로_결제가_생성된다(){
        //given

        // 결제 공통 정보
        int amount = 1000;
        String orderId = "order1";
        String paymentKey = "paymentKey";
        String method = "카드";
        OffsetDateTime approvedAt = OffsetDateTime.now();
        String status = "DONE";

        // 핸드폰 결제 정보(결제 상세 정보)
        String customerMobilePhone = "010-1234-5678";
        String settlementStatus = "정산완료";
        String receiptUrl = "http://naver.com";

        PaymentDetail mobilePhoneDetail = MPPayDetail.builder().
                customerMobilePhone(customerMobilePhone).
                settlementStatus(settlementStatus).
                receiptUrl(receiptUrl).
                build();

        PaymentContext paymentContext = PaymentContext.builder().
                amount(amount).
                orderId(orderId).
                paymentKey(paymentKey).
                method(method).
                approvedAt(approvedAt).
                status(status).
                paymentDetail(mobilePhoneDetail).
                build();

        // pay 생성을 위한 dummy 주문
        Order order = new Order();

        // when
        Pay pay = Pay.create(order, paymentContext);

        // then
        assertEquals(paymentKey, pay.getPaymentKey());
        assertEquals(approvedAt, pay.getApprovedAt());
        assertEquals(amount, pay.getPayAmount());
        assertEquals(PayMethod.CARD, pay.getPayMethod());
        assertEquals(PayStatus.SUCCESS, pay.getPayStatus());
    }

    @Test
    void 등록되지_않은_결제수단이_응답으로_왔다면_결제_생성중_예외발생(){
        //given
        String method = "테블릿";
        PaymentContext paymentContext = PaymentContext.builder()
                .method(method).build();
        Order order = new Order();

        // when && then
        assertThrows(UnsupportedPayMethodException.class, () -> Pay.create(order, paymentContext));
    }

    @Test
    @DisplayName("SUCCESS 상태에서 결제 취소가 가능하다")
    void success_to_canceled() {
        Pay payment = new Pay();
        payment.setPayStatus(PayStatus.SUCCESS);

        payment.cancel();

        assertEquals(PayStatus.CANCELED, payment.getPayStatus());
    }

}