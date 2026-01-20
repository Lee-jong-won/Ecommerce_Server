package jongwon.e_commerce.payment.exception;

import jongwon.e_commerce.common.exception.ErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;

public class TossPaymentNotFoundException extends TossPaymentException {
    public TossPaymentNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
