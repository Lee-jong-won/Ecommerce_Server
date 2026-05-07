package jongwon.e_commerce.payment.exception;

public abstract class PayApproveException extends RuntimeException {
    public PayApproveException(String message) {
        super(message);
    }
    public PayApproveException(String message, Throwable cause) {
        super(message, cause);
    }
}
