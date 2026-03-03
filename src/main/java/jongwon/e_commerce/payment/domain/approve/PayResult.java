package jongwon.e_commerce.payment.domain.approve;

import jongwon.e_commerce.payment.domain.PayMethod;
import jongwon.e_commerce.payment.domain.detail.PaymentDetail;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class PayResult {
    private OffsetDateTime approvedAt;
    private PayMethod payMethod;
    private PaymentDetail paymentDetail;
}
