package jongwon.e_commerce.payment.application.approve.result.context;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MobilePhoneDetail implements PaymentDetail {
    private String customerMobilePhone;
    private String settlementStatus;
    private String receiptUrl;
}
