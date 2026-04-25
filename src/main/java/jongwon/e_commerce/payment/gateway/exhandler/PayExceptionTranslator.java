package jongwon.e_commerce.payment.gateway.exhandler;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import org.springframework.web.client.RestClientException;

public interface PayExceptionTranslator {
    PayApproveOutcome translate(RestClientException restClientException);
}
