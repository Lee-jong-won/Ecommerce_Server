package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.infrastructure.gateway.PaymentClient;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;

public class StubPaymentRestApproveClientConnTimeout implements PaymentClient {
    @Override
    public PayApproveOutcome callPayApprovalApi(PayApproveAttempt request) {
        SocketTimeoutException cause = new SocketTimeoutException("connect timed out");
        ResourceAccessException ex = new ResourceAccessException("I/O error", cause);
        throw ex;
    }
}
