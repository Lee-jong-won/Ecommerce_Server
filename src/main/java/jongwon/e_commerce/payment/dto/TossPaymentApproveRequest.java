package jongwon.e_commerce.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TossPaymentApproveRequest {

    private String orderId;
    private String paymentKey;
    private long amount;

    public TossPaymentApproveRequest(String orderId, String paymentKey, long amount){
        this.orderId = orderId;
        this.paymentKey = paymentKey;
        this.amount = amount;
    }
}
