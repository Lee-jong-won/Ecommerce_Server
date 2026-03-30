package jongwon.e_commerce.payment.domain.detail;

import jongwon.e_commerce.payment.domain.Pay;
import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.repository.jpa.entity.MPPayEntity;
import jongwon.e_commerce.payment.repository.jpa.entity.PayDetailEntity;
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

    @Override
    public PayMethod getPayMethod() {
        return pay.getPayMethod();
    }

    @Override
    public void setPay(Pay pay) {
        this.pay = pay;
    }

    @Override
    public PayDetailEntity toEntity() {
        return MPPayEntity.from(this);
    }

    public static MPPay createMPPay(
            String customerMobilePhone,
            String settlementStatus,
            String receiptUrl){

        return MPPay.builder().
                customerMobilePhone(customerMobilePhone).
                settlementStatus(settlementStatus).
                receiptUrl(receiptUrl).build();
    }

}
