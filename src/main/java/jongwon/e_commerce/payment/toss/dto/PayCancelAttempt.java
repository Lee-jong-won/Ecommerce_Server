package jongwon.e_commerce.payment.toss.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayCancelAttempt {

    public String paymentKey;
    public String idempotencyKey;
    public String cancelReason;

    public PayCancelAttempt(String paymentKey, String idempotencyKey, String cancelReason){
        this.paymentKey = paymentKey;
        this.idempotencyKey = idempotencyKey;
        this.cancelReason = cancelReason;
    }
}
