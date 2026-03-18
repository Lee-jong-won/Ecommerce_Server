package jongwon.e_commerce.payment.application.approve.execute;

import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import org.springframework.web.client.RestClientException;

public interface PayApproveExceptionTranslator {
    PayApproveOutcome translate(RestClientException restClientException);
}
