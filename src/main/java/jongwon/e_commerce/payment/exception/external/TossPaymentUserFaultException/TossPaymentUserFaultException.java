package jongwon.e_commerce.payment.exception.external.TossPaymentUserFaultException;

import jongwon.e_commerce.payment.exception.external.PaymentErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;

public class TossPaymentUserFaultException extends TossPaymentException {
    public TossPaymentUserFaultException(PaymentErrorCode errorCode) {
        super(errorCode);
    }
}
