package jongwon.e_commerce.payment.exception.external;

import jongwon.e_commerce.common.exception.ErrorCode;
import jongwon.e_commerce.common.exception.InfrastructureException;

public class TossPaymentException extends InfrastructureException {
    public TossPaymentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
