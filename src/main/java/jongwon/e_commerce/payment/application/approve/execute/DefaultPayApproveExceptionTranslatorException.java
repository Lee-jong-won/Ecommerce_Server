package jongwon.e_commerce.payment.application.approve.execute;

import jongwon.e_commerce.payment.domain.approve.decision.PayApproveTimeout;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentTimeoutException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

import java.net.SocketTimeoutException;

@Component
@Service
public class DefaultPayApproveExceptionTranslatorException {

    public TossPaymentException translate(RestClientException restClientException){
        if(restClientException instanceof ResourceAccessException
                && isReadTimeout(restClientException.getCause()))
            return new TossPaymentTimeoutException("타임아웃 발생");
        return new TossPaymentException("결제 실패 예외");
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
