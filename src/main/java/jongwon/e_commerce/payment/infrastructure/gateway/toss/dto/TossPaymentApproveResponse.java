package jongwon.e_commerce.payment.infrastructure.gateway.toss.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TossPaymentApproveResponse {

    private String method;   // 카드
    private String approvedAt; // 결제 승인 일자
    private String orderName; // 주문 이름
    private long amount; // 결제 금액
    private MobilePhoneDto mobilePhone; // 핸드폰 결제 정보

    @Getter
    @AllArgsConstructor
    public static class MobilePhoneDto {
        public String customerMobilePhone;
        public String settlementStatus;
        public String receiptUrl;
    }
}

