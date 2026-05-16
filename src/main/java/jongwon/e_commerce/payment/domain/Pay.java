package jongwon.e_commerce.payment.domain;

import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Builder
public class Pay {

    private Long id;
    private PayMethod payMethod;
    private long payAmount;
    private Long payRequestId;
    private OffsetDateTime approvedAt;
    private Map<String, Object> paymentDetail;
    private LocalDateTime createdAt;

    public static Pay from(
            PayResult.PayResultCommon payResultCommon,
            Map<String, Object> paymentDetail,
            Long payRequestId){

        return Pay.builder()
                .payRequestId(payRequestId)
                .payAmount(payResultCommon.getAmount())
                .payMethod(payResultCommon.getPayMethod())
                .approvedAt(payResultCommon.getApprovedAt())
                .paymentDetail(paymentDetail)
                .build();
    }
}
