package jongwon.e_commerce.payment.application.approve.external;

import jongwon.e_commerce.payment.domain.approve.result.fail.*;
import jongwon.e_commerce.payment.domain.approve.result.ignore.ConnectionRequestTimeout;
import jongwon.e_commerce.payment.domain.approve.result.ignore.ConnectionTimeout;
import jongwon.e_commerce.payment.domain.approve.result.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.result.unknown.PayApproveTimeout;
import jongwon.e_commerce.payment.domain.approve.result.ignore.UnknownRestClientError;
import jongwon.e_commerce.payment.exception.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
@RequiredArgsConstructor
public class DefaultPayApproveExceptionTranslator implements PayApproveExceptionTranslator {

    private final ObjectMapper mapper;

    @Override
    public PayApproveOutcome translate(RestClientException e) {
        // 1. 네트워크 계열
        if (e instanceof ResourceAccessException) {

            Throwable cause = e.getCause();
            if (ExceptionUtils.isReadTimeout(cause)) {
                return new PayApproveTimeout();
            }

            if (ExceptionUtils.isConnectTimeout(cause)) {
                return new ConnectionTimeout();
            }

            if (ExceptionUtils.isConnectionRequestTimeout(cause)) {
                return new ConnectionRequestTimeout();
            }
        }

        // 2. HTTP 응답 기반
        if (e instanceof RestClientResponseException) {
            return translateFromErrorResponse((RestClientResponseException) e);
        }

        // 3. 알 수 없는 경우
        log.error("RestClientException occurred", e);
        return new UnknownRestClientError();
    }

    private PayApproveOutcome translateFromErrorResponse(RestClientResponseException ex) {
        String body = ex.getResponseBodyAsString();
        try {
            JsonNode json = mapper.readTree(body);
            JsonNode codeNode = json.get("code");
            String code = (codeNode != null && codeNode.isTextual())
                    ? codeNode.asText()
                    : "INVALID_ERROR_RESPONSE";
            return mapToOutcome(code);
        } catch (JacksonException e) {
            return new JsonParsingError();
        }
    }

    /**
     * 외부 error code → 도메인 타입 매핑
     */
    private PayApproveOutcome mapToOutcome(String code) {
        return switch (code) {
            case "REJECT_CARD_PAYMENT" -> new InsufficientBalance();
            case "INVALID_REJECT_CARD" -> new InvalidCard();
            case "INVALID_ERROR_RESPONSE" -> new InvalidErrorResponse();
            default -> new UnknownErrorCode();
        };
    }

}
