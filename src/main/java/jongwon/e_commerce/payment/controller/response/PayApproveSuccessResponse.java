package jongwon.e_commerce.payment.controller.response;

import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Builder
@Getter
public class PayApproveSuccessResponse {

    private final long payAmount;
    private final String orderName;
    private final String approvedAt;

    public static PayApproveSuccessResponse from(PayResult result) {
        PayResult.PayResultCommon common = result.getPayResultCommon();
        return PayApproveSuccessResponse.builder()
                .payAmount(common.getAmount())
                .orderName(common.getOrderName())
                .approvedAt(common.getApprovedAt().toString())
                .build();
    }
}
