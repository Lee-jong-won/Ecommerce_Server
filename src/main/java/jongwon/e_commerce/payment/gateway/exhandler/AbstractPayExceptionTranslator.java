package jongwon.e_commerce.payment.gateway.exhandler;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.ignore.UnknownRestClientError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPayExceptionTranslator implements PayExceptionTranslator {

    protected abstract List<PaymentExceptionHandler> getHandlers();

    @Override
    public PayApproveOutcome translate(RestClientException restClientException) {
        return getHandlers().stream()
                .filter(handler -> handler.supports(restClientException))
                .findFirst()
                .map(handler -> handler.handle(restClientException))
                .orElse(new UnknownRestClientError());
    }
}
