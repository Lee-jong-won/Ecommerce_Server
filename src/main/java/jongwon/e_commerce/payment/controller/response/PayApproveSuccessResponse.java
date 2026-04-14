package jongwon.e_commerce.payment.controller.response;

import lombok.Builder;

@Builder
public class PayApproveSuccessResponse {

    private final long orderPrice;
    private final long payAmount;
    private final String orderName;
}
