package jongwon.e_commerce.payment.exception;

public class PayErrorResponseParsingException extends PayGatewayException {
    public PayErrorResponseParsingException(String message) {
        super(message);
    }
}
