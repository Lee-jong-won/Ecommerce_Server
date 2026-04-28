package jongwon.e_commerce.payment.gateway.exhandler;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import org.springframework.web.client.RestClientException;

public interface PaymentExceptionHandler {
    // 실제 예외 처리 로직
    PayApproveOutcome handle(RestClientException e);
}
