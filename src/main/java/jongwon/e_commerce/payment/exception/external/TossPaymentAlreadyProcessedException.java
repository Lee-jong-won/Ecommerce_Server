package jongwon.e_commerce.payment.exception.external;

import jongwon.e_commerce.common.exception.ErrorCode;

public class TossPaymentAlreadyProcessedException extends TossPaymentException {
    public TossPaymentAlreadyProcessedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
