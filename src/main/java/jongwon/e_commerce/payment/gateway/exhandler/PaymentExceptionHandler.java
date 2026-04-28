package jongwon.e_commerce.payment.gateway.exhandler;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import org.springframework.web.client.RestClientException;

public interface PaymentExceptionHandler {
    // 본인이 처리할 수 있는 예외인지 확인
    boolean supports(RestClientException e);

    // 실제 예외 처리 로직
    PayApproveOutcome handle(RestClientException e);
}
