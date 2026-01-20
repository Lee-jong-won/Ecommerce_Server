package jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException;

import jongwon.e_commerce.common.exception.ErrorCode;

public class TossApiTimeoutException extends TossPaymentRetryableException{
    public TossApiTimeoutException(ErrorCode errorCode) {
        super(errorCode);
    }
}
