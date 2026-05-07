package jongwon.e_commerce.payment.exception;

public class PayTimeoutException extends PayUnknownOutcomeException {
    public PayTimeoutException() {
        super("결제 응답을 받지 못했습니다.");
    }
}
