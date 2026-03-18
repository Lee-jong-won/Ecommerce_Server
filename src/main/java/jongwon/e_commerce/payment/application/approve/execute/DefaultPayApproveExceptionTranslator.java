package jongwon.e_commerce.payment.application.approve.execute;

import jongwon.e_commerce.payment.domain.approve.decision.PayApproveFail;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.decision.PayApproveTimeout;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;

@Component
public class DefaultPayApproveExceptionTranslator implements PayApproveExceptionTranslator {
    @Override
    public PayApproveOutcome translate(RestClientException restClientException) {
        // 네트워크 타임아웃
        if(restClientException instanceof ResourceAccessException
                && isReadTimeout(restClientException.getCause()))
            return new PayApproveTimeout();

        // 그 외 예외
        return new PayApproveFail();
    }

    private boolean isReadTimeout(Throwable cause) {
        while (cause != null) {
            if (cause instanceof SocketTimeoutException) {
                return true;
            }
            cause = cause.getCause();
        }
        return false;
    }
}
