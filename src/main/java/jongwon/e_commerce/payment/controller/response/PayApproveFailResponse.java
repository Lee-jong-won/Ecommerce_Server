package jongwon.e_commerce.payment.controller.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayApproveFailResponse {
    private final String code;
    private final String message;
}
