package jongwon.e_commerce.payment.infrastructure.gateway.exhandler;

import jongwon.e_commerce.payment.exception.PayApproveException;
import org.springframework.web.client.RestClientResponseException;

public interface PaymentErrorResponseHandler {
    PayApproveException handle(RestClientResponseException e);
}
