package jongwon.e_commerce.payment.exception;

public class PayGatewayException extends PayApproveException {
    public PayGatewayException(String message) {
        super(message);
    }
}
