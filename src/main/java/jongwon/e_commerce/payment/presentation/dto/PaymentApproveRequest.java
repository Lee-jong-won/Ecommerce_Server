package jongwon.e_commerce.payment.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentApproveRequest {

    private Long orderId;
    private String orderName;
    private String payOrderId;
    private String paymentKey;
    private int amount;

    public PaymentApproveRequest(Long orderId, String payOrderId, String paymentKey,
                                 String orderName, int amount){
        this.orderId = orderId;
        this.payOrderId = payOrderId;
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.amount = amount;
    }
}
