package jongwon.e_commerce.payment.exception.external;

public class TossPaymentTimeoutException extends TossPaymentNetworkException {
    public TossPaymentTimeoutException(String message) {
        super(message);
    }
}
