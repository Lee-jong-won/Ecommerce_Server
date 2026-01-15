package jongwon.e_commerce.payment.exception;

import jongwon.e_commerce.common.exception.InfrastructureException;

public class TossApiNetworkException extends InfrastructureException {
    public TossApiNetworkException() {
    }
    public TossApiNetworkException(String message) {
        super(message);
    }
}
