package jongwon.e_commerce.payment.infrastructure.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
public class PayApproveAttempt {

    private String orderId;
    private String paymentKey;
    private String pgType;
    private long amount;


    @Builder
    public PayApproveAttempt(@JsonProperty("paymentKey") String paymentKey,
                             @JsonProperty("orderId")String orderId,
                             @JsonProperty("pgType")String pgType,
                             @JsonProperty("amount")long amount){
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.pgType = pgType;
        this.amount = amount;
    }
}
