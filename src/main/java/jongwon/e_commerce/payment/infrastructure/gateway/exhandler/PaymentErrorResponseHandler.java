package jongwon.e_commerce.payment.infrastructure.gateway.exhandler;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

public interface PaymentErrorResponseHandler {
    PayApproveOutcome handle(RestClientResponseException e);
}
