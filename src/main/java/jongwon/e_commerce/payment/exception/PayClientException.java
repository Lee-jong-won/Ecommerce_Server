package jongwon.e_commerce.payment.exception;

import lombok.Getter;

@Getter
public class PayClientException extends PayApproveException {
    private final PayErrorCode errorCode;

    public PayClientException(PayErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }
}
