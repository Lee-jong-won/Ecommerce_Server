package jongwon.e_commerce.payment.gateway.exhandler;


import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.ignore.ConnectionRequestTimeout;
import jongwon.e_commerce.payment.domain.approve.outcome.ignore.ConnectionTimeout;
import jongwon.e_commerce.payment.domain.approve.outcome.ignore.UnknownRestClientError;
import jongwon.e_commerce.payment.domain.approve.outcome.unknown.ReadTimeout;
import jongwon.e_commerce.payment.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;

@Component("networkExceptionHandler")
public class NetworkExceptionHandler implements PaymentExceptionHandler {

    @Override
    public boolean supports(RestClientException e) {
        return e instanceof ResourceAccessException;
    }

    @Override
    public PayApproveOutcome handle(RestClientException e) {
        Throwable cause = e.getCause();

        if (ExceptionUtils.isReadTimeout(cause)) {
            return new ReadTimeout();
        }
        if (ExceptionUtils.isConnectTimeout(cause)) {
            return new ConnectionTimeout();
        }
        if (ExceptionUtils.isConnectionRequestTimeout(cause)) {
            return new ConnectionRequestTimeout();
        }

        return new UnknownRestClientError();
    }
}
