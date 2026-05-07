package jongwon.e_commerce.payment.infrastructure.gateway.exhandler;

import jongwon.e_commerce.payment.exception.PayApproveException;
import jongwon.e_commerce.payment.exception.PayUnknownOutcomeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPayExceptionTranslator {
    protected abstract PaymentErrorResponseHandler getErrorResponseHandler();
    public PayApproveException translate(RestClientException restClientException) {
        if (restClientException instanceof ResourceAccessException e)
            return IOExceptionHandler.handle(e);

        if(restClientException instanceof RestClientResponseException)
            return getErrorResponseHandler().handle((RestClientResponseException) restClientException);

        return new PayUnknownOutcomeException("알 수 없는 오류가 발생했습니다.");
    }
}
