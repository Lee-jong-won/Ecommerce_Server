package jongwon.e_commerce.payment.exception.external.TossPaymentSystemException;

import jongwon.e_commerce.common.exception.ErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;

public class TossPaymentSystemException extends TossPaymentException {
    public TossPaymentSystemException(ErrorCode errorCode) {
        super(errorCode);
    }
}
