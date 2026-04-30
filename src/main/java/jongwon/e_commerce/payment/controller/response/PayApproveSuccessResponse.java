package jongwon.e_commerce.payment.controller.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PayApproveSuccessResponse {
    private final long payAmount;
    private final String orderName;
    private final String approvedAt;
}
