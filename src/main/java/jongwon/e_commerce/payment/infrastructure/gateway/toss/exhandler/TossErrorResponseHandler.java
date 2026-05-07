package jongwon.e_commerce.payment.infrastructure.gateway.toss.exhandler;

import jongwon.e_commerce.payment.exception.PayApproveException;
import jongwon.e_commerce.payment.exception.PayClientException;
import jongwon.e_commerce.payment.exception.PayErrorCode;
import jongwon.e_commerce.payment.exception.PayGatewayException;
import jongwon.e_commerce.payment.exception.PayErrorResponseParsingException;
import jongwon.e_commerce.payment.infrastructure.gateway.exhandler.PaymentErrorResponseHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component("tossErrorResponseHandler")
@RequiredArgsConstructor
public class TossErrorResponseHandler implements PaymentErrorResponseHandler {

    private final ObjectMapper mapper;

    @Override
    public PayApproveException handle(RestClientResponseException e) {
        String body = e.getResponseBodyAsString();
        try {
            String code = mapper.readTree(body).at("/code").asText("UNKNOWN");
            return switch (code) {
                case "REJECT_CARD_PAYMENT" -> new PayClientException(PayErrorCode.INSUFFICIENT_BALANCE);
                case "INVALID_REJECT_CARD" -> new PayClientException(PayErrorCode.INVALID_CARD);
                default -> new PayGatewayException("등록되지 않은 에러 코드: " + code);
            };
        } catch (JacksonException jacksonException) {
            return new PayErrorResponseParsingException();
        }
    }
}
