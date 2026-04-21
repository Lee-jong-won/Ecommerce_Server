package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.toss.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.PaymentApproveClient;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;

public class StubPaymentRestApproveClientConnTimeout implements PaymentApproveClient {
    @Override
    public TossPaymentApproveResponse callPayApprovalApi(PayApproveAttempt request, String idempotencyKey) {
        SocketTimeoutException cause = new SocketTimeoutException("connect timed out");
        ResourceAccessException ex = new ResourceAccessException("I/O error", cause);
        throw ex;
    }
}
