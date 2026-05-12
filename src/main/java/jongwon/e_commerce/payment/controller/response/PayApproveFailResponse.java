package jongwon.e_commerce.payment.controller.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class PayApproveFailResponse {
    private final String code;
    private final String message;
}
