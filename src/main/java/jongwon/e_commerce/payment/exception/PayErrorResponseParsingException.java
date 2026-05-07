package jongwon.e_commerce.payment.exception;

public class PayErrorResponseParsingException extends PayGatewayException {
    public PayErrorResponseParsingException() {
        super("결제 에러 응답 파싱에 실패했습니다.");
    }
}
