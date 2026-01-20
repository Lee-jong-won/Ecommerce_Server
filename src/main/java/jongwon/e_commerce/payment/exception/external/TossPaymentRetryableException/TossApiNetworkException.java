package jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException;

import jongwon.e_commerce.common.exception.ErrorCode;

public class TossApiNetworkException extends TossPaymentRetryableException {

    public TossApiNetworkException(ErrorCode errorCode) {
        super(errorCode);
    }
}
