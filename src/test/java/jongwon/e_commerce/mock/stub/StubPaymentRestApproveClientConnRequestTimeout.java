package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.toss.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.PaymentApproveClient;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import org.apache.hc.core5.http.ConnectionRequestTimeoutException;
import org.springframework.web.client.ResourceAccessException;

public class StubPaymentRestApproveClientConnRequestTimeout implements PaymentApproveClient {

    @Override
    public TossPaymentApproveResponse callPayApprovalApi(PayApproveAttempt request, String idempotencyKey) {
        ConnectionRequestTimeoutException connectionRequestTimeoutException = new ConnectionRequestTimeoutException();
        ResourceAccessException resourceAccessException = new ResourceAccessException("I/O Error", connectionRequestTimeoutException);
        throw resourceAccessException;
    }
}
