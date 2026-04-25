package jongwon.e_commerce.payment.gateway.exhandler;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.ignore.UnknownRestClientError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPayExceptionTranslator implements PayExceptionTranslator {

    private final NetworkExceptionHandler networkExceptionHandler;

    protected PGErrorCodeParser getErrorCodeParser() {
        return null;
    }

    @Override
    public PayApproveOutcome translate(RestClientException restClientException) {
        // 1. 네트워크 계열 분리 처리
        if (restClientException instanceof ResourceAccessException rae) {
            return networkExceptionHandler.handle(rae);
        }

        // 2. HTTP 응답 기반 분리 처리
        if (restClientException instanceof RestClientResponseException rcre) {
            return getErrorCodeParser().parse(rcre.getResponseBodyAsString());
        }

        log.error("Unhandled RestClientException", restClientException);
        return new UnknownRestClientError();
    }
}
