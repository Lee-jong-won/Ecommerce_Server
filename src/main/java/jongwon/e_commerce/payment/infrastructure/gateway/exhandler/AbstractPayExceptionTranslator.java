package jongwon.e_commerce.payment.infrastructure.gateway.exhandler;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPayExceptionTranslator {
    protected abstract PaymentErrorResponseHandler getErrorResponseHandler();
    public PayApproveOutcome translate(RestClientException restClientException) {
        // 1. I/O 에러는, networkExceptionHandler를 통해 처리
        if(restClientException instanceof ResourceAccessException)
            return IOExceptionHandler.handle((ResourceAccessException) restClientException);

        // 2. 에러 응답은 ErrorResponseHandler를 통해 처리
        return getErrorResponseHandler().handle((RestClientResponseException) restClientException);
    }
}
