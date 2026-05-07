package jongwon.e_commerce.payment.controller.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class PayApproveFailResponse {
    private final String code;
    private final String message;
    private final RetryStrategy retryStrategy;

    public enum RetryStrategy{
        NEW_PAYMENT_KEY,
        SAME_PAYMENT_KEY;
    }
}
