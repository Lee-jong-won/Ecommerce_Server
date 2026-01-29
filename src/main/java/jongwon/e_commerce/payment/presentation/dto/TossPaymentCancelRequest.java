package jongwon.e_commerce.payment.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TossPaymentCancelRequest {
    public String paymentKey;
    public String cancelReason;

    public TossPaymentCancelRequest(String paymentKey, String cancelReason){
        this.paymentKey = paymentKey;
        this.cancelReason = cancelReason;
    }
}
