package jongwon.e_commerce.payment.infrastructure.gateway.dto.result;

import jongwon.e_commerce.payment.domain.PayMethod;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Builder
public class PayResult {

    private PayResultCommon payResultCommon;
    private Map<String, Object> paymentDetail;

    @Getter
    @Builder
    public static class PayResultCommon {
        private String orderName;
        private long amount;
        private OffsetDateTime approvedAt;
        private PayMethod payMethod;
    }

}
