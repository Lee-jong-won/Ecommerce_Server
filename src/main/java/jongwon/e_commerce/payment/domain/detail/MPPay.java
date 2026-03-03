package jongwon.e_commerce.payment.domain.detail;

import jongwon.e_commerce.payment.domain.Pay;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MPPay implements PaymentDetail {

    private Long id;
    private Pay pay;
    private String customerMobilePhone;
    private String settlementStatus;
    private String receiptUrl;

    public static MPPay from(String customerMobilePhone, String settlementStatus, String receiptUrl){
        return MPPay.builder().
                customerMobilePhone(customerMobilePhone).
                settlementStatus(settlementStatus).
                receiptUrl(receiptUrl).build();
    }
}
