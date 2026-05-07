package jongwon.e_commerce.payment.infrastructure.gateway.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@EqualsAndHashCode
public class PayApproveAttempt {

    // [공통 필드]
    private String orderName;
    private String paymentKey;
    private String pgType;
    private long amount;

    // [나이스 전용 필드] [toDo]

    @Builder
    public PayApproveAttempt(@JsonProperty("paymentKey") String paymentKey,
                             @JsonProperty("orderName")String orderName,
                             @JsonProperty("pgType")String pgType,
                             @JsonProperty("amount")long amount){
        this.paymentKey = paymentKey;
        this.orderName = orderName;
        this.pgType = pgType;
        this.amount = amount;
    }
}
