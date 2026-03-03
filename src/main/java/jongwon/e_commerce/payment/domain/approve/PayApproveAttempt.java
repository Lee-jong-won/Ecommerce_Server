package jongwon.e_commerce.payment.domain.approve;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PayApproveAttempt {

    private String orderId;
    private String paymentKey;
    private long amount;

    @Builder
    public PayApproveAttempt(@JsonProperty("paymentKey") String paymentKey,
                             @JsonProperty("orderId")String orderId,
                             @JsonProperty("amount")long amount){
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }
}
