package jongwon.e_commerce.payment.exception;

public class UnsupportedPayMethodException extends RuntimeException {
    public UnsupportedPayMethodException() {
    }
    public UnsupportedPayMethodException(String message) {
        super(message);
    }

}
