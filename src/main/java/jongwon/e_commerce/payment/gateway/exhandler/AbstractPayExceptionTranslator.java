package jongwon.e_commerce.payment.gateway.exhandler;

import jakarta.annotation.PostConstruct;
import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.ignore.UnknownRestClientError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPayExceptionTranslator {

    private final NetworkExceptionHandler networkExceptionHandler;
    protected abstract PaymentExceptionHandler getErrorResponseHandler();
    public PayApproveOutcome translate(RestClientException restClientException) {
        // 1. I/O 에러는, networkExceptionHandler를 통해 처리
        if(restClientException instanceof ResourceAccessException)
            return networkExceptionHandler.handle(restClientException);

        // 2. 에러 응답은 ErrorResponseHandler를 통해 처리
        return getErrorResponseHandler().handle(restClientException);
    }
}
