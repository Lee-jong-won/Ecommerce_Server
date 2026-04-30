package jongwon.e_commerce.payment.infrastructure.gateway.toss.dto;

import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TossPaymentApproveRequest {

    private String orderId;
    private String paymentKey;
    private long amount;

    public static TossPaymentApproveRequest from(PayApproveAttempt request){
        return TossPaymentApproveRequest.
                builder().
                paymentKey(request.getPaymentKey()).
                orderId(request.getOrderId()).
                amount(request.getAmount()).
                build();
    }

}
