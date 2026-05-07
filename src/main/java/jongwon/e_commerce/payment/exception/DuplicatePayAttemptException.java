package jongwon.e_commerce.payment.exception;

public class DuplicatePayAttemptException extends PayApproveException {
    public DuplicatePayAttemptException(String message) {
        super(message);
    }
}
