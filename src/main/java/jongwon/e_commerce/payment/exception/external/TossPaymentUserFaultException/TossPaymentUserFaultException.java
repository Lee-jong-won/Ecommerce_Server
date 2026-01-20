package jongwon.e_commerce.payment.exception.external.TossPaymentUserFaultException;

import jongwon.e_commerce.common.exception.ErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;

public class TossPaymentUserFaultException extends TossPaymentException {
    public TossPaymentUserFaultException(ErrorCode errorCode) {
        super(errorCode);
    }
}
