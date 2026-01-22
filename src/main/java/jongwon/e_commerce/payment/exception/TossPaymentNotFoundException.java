package jongwon.e_commerce.payment.exception;

import jongwon.e_commerce.payment.exception.external.PaymentErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;

public class TossPaymentNotFoundException extends TossPaymentException {
    public TossPaymentNotFoundException(PaymentErrorCode errorCode) {
        super(errorCode);
    }
}
