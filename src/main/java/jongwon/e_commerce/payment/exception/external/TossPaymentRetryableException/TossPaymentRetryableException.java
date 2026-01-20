package jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException;

import jongwon.e_commerce.common.exception.ErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;

public class TossPaymentRetryableException extends TossPaymentException {
    public TossPaymentRetryableException(ErrorCode errorCode) {
        super(errorCode);
    }
}
