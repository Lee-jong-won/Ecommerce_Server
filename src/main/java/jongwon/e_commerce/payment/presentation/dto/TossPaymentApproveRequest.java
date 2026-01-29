package jongwon.e_commerce.payment.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TossPaymentApproveRequest {

    private Long orderId;
    private String orderName;
    private String payOrderId;
    private String paymentKey;
    private String idempotencyKey;
    private long amount;

    public TossPaymentApproveRequest(Long orderId, String payOrderId, String paymentKey,
                                     String orderName, String idempotencyKey, long amount){
        this.orderId = orderId;
        this.payOrderId = payOrderId;
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.idempotencyKey = idempotencyKey;
        this.amount = amount;
    }
}
