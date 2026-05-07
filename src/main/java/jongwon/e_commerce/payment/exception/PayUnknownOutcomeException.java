package jongwon.e_commerce.payment.exception;

public class PayUnknownOutcomeException extends PayApproveException{
    public PayUnknownOutcomeException(String message) {
        super(message);
    }
}
