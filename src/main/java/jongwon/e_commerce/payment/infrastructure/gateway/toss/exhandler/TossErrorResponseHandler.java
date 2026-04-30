package jongwon.e_commerce.payment.infrastructure.gateway.toss.exhandler;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.InsufficientBalance;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.InvalidCard;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.JsonParsingError;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.UnknownErrorCode;
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
    public PayApproveOutcome handle(RestClientResponseException e) {
        String body = e.getResponseBodyAsString();
        try {
            String code = mapper.readTree(body).at("/code").asText("UNKNOWN");
            return switch (code) {
                case "REJECT_CARD_PAYMENT" -> new InsufficientBalance();
                case "INVALID_REJECT_CARD" -> new InvalidCard();
                default -> new UnknownErrorCode();
            };
        } catch (JacksonException jacksonException) {
            return new JsonParsingError();
        }
    }
}
