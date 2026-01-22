package jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException;

import jongwon.e_commerce.payment.exception.external.PaymentErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;

public class TossPaymentRetryableException extends TossPaymentException {
    public TossPaymentRetryableException(PaymentErrorCode errorCode) {
        super(errorCode);
    }
}
