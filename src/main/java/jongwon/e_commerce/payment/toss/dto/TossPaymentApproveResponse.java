package jongwon.e_commerce.payment.toss.dto;

import jongwon.e_commerce.payment.domain.*;
import jongwon.e_commerce.payment.domain.detail.MPPay;
import jongwon.e_commerce.payment.domain.approve.PayResult;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import jongwon.e_commerce.payment.exception.UnsupportedPayMethodException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class TossPaymentApproveResponse {

    private String method;   // 카드
    private OffsetDateTime approvedAt; // 결제 승인 일자
    private MobilePhoneDto mobilePhone; // 핸드폰 결제 정보

    public PayResult toPayResult(){
        PayMethod payMethod = PayMethod.from(method);
        return PayResult.builder().
                payMethod(payMethod).
                approvedAt(approvedAt).
                paymentDetail(extractPaymentDetail()).
                build();
    }

    public PaymentDetail extractPaymentDetail() {
        PayMethod payMethod = PayMethod.from(this.method);
        switch (payMethod) {
            case MOBILE :
                return mobilePhone.toPaymentDetail();
            default:
                throw new UnsupportedPayMethodException("지원하지 않는 결제 수단입니다.");
        }
    }

    @Getter
    @AllArgsConstructor
    public static class MobilePhoneDto implements TossPaymentDetail {
        public String customerMobilePhone;
        public String settlementStatus;
        public String receiptUrl;
        public PaymentDetail toPaymentDetail(){
            return MPPay.
                    from(customerMobilePhone, settlementStatus, receiptUrl);
        }
    }
}

