package jongwon.e_commerce.payment.gateway.toss;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.InsufficientBalance;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.InvalidCard;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.JsonParsingError;
import jongwon.e_commerce.payment.domain.approve.outcome.fail.UnknownErrorCode;
import jongwon.e_commerce.payment.gateway.exhandler.PGErrorCodeParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class TossErrorCodeParser implements PGErrorCodeParser {

    private final ObjectMapper mapper;

    @Override
    public PayApproveOutcome parse(String body) {
        try {
            String code = mapper.readTree(body).at("/code").asText("UNKNOWN");
            return switch (code) {
                case "REJECT_CARD_PAYMENT" -> new InsufficientBalance();
                case "INVALID_REJECT_CARD" -> new InvalidCard();
                default -> new UnknownErrorCode();
            };
        } catch (Exception e) {
            return new JsonParsingError();
        }
    }
}
