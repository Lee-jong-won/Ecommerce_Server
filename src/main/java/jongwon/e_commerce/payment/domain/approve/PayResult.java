package jongwon.e_commerce.payment.domain.approve;

import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class PayResult {
    private PayResultCommon payResultCommon;
    private PaymentDetail paymentDetail;

    @Getter
    @Builder
    public static class PayResultCommon {
        private OffsetDateTime approvedAt;
        private PayMethod payMethod;
    }

}
