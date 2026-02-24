package jongwon.e_commerce.payment.toss.dto;

import jongwon.e_commerce.payment.application.approve.result.context.MobilePhoneDetail;
import jongwon.e_commerce.payment.application.approve.result.context.PaymentDetail;
import jongwon.e_commerce.payment.application.approve.result.context.PaymentContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class TossPaymentApproveResponse {
    private long amount;
    private String orderId;
    private String paymentKey;
    private String method;   // 카드
    private OffsetDateTime approvedAt; // 결제 승인 일자
    private String status; // 결제 상태
    private MobilePhoneDto mobilePhone; // 핸드폰 결제 정보

    public PaymentContext toPaymentContext(){
        return PaymentContext.builder().
                amount(this.amount).
                orderId(this.orderId).
                paymentKey(this.paymentKey).
                method(this.method).
                approvedAt(this.approvedAt).
                status(this.status).
                paymentDetail(convertToDetail()).
                build();
    }
    private PaymentDetail convertToDetail() {
        switch (this.method) {
            case "휴대폰":
                return mobilePhone.toPaymentDetail();
            default:
                return null;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class MobilePhoneDto{
        public String customerMobilePhone;
        public String settlementStatus;
        public String receiptUrl;

        public MobilePhoneDetail toPaymentDetail(){
            return MobilePhoneDetail.builder().
                    customerMobilePhone(this.customerMobilePhone)
                    .settlementStatus(this.settlementStatus)
                    .receiptUrl(this.receiptUrl)
                    .build();
        }
    }
}

