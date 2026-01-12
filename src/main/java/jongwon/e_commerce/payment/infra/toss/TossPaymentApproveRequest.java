package jongwon.e_commerce.payment.infra.toss;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TossPaymentApproveRequest {
    private String paymentKey;
    private String orderId;
    private int amount;
}
