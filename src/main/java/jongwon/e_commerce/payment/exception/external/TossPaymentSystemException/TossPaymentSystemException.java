package jongwon.e_commerce.payment.exception.external.TossPaymentSystemException;

import jongwon.e_commerce.payment.exception.external.PaymentErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;

public class TossPaymentSystemException extends TossPaymentException {
    public TossPaymentSystemException(PaymentErrorCode errorCode) {
        super(errorCode);
    }
}
