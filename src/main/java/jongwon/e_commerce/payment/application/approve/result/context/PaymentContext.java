package jongwon.e_commerce.payment.application.approve.result.context;

import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Builder
@Getter
public class PaymentContext {
    private long amount;
    private String orderId;
    private String paymentKey;
    private String method;   // 카드
    private OffsetDateTime approvedAt; // 결제 승인 일자
    private String status; // 결제 상태
    private PaymentDetail paymentDetail;
}
