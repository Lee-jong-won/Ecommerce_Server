package jongwon.e_commerce.payment.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TossPaymentApproveRequest {

    private String payOrderId;
    private String paymentKey;
    private String idempotencyKey;
    private long amount;

    public TossPaymentApproveRequest(String payOrderId, String paymentKey,
                                     String idempotencyKey, long amount){
        this.payOrderId = payOrderId;
        this.paymentKey = paymentKey;
        this.idempotencyKey = idempotencyKey;
        this.amount = amount;
    }
}
