package jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException;

import jongwon.e_commerce.payment.exception.external.PaymentErrorCode;

public class TossApiNetworkException extends TossPaymentRetryableException {

    public TossApiNetworkException(PaymentErrorCode errorCode) {
        super(errorCode);
    }
}
