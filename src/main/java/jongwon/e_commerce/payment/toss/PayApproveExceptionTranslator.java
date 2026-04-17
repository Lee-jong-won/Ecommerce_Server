package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.domain.approve.result.PayApproveOutcome;
import org.springframework.web.client.RestClientException;

public interface PayApproveExceptionTranslator {
    PayApproveOutcome translate(RestClientException restClientException);
}
