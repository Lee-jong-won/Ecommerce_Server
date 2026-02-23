package jongwon.e_commerce.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class TossPaymentApproveResponse {
    private String paymentKey;
    private String orderId;
    private String method;   // 카드
    private OffsetDateTime approvedAt; // 결제 승인 일자
    private String status; // 결제 상태
    private MobilePhoneDto mobilePhone; // 핸드폰 결제 정보

    @Getter
    @AllArgsConstructor
    public static class MobilePhoneDto{
        public String customerMobilePhone;
        public String settlementStatus;
        public String receiptUrl;
    }
}

