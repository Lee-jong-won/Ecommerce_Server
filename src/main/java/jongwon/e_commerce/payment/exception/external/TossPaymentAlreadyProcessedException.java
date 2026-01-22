package jongwon.e_commerce.payment.exception.external;

import jongwon.e_commerce.payment.exception.external.PaymentErrorCode;

public class TossPaymentAlreadyProcessedException extends TossPaymentException {
    public TossPaymentAlreadyProcessedException(PaymentErrorCode errorCode) {
        super(errorCode);
    }
}
