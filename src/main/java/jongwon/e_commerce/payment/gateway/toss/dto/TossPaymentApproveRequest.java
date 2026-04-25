package jongwon.e_commerce.payment.gateway.toss.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TossPaymentApproveRequest {

    private String orderId;
    private String paymentKey;
    private long amount;

}
