package jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException;

import jongwon.e_commerce.payment.exception.external.PaymentErrorCode;

public class TossApiTimeoutException extends TossPaymentRetryableException{
    public TossApiTimeoutException(PaymentErrorCode errorCode) {
        super(errorCode);
    }
}
