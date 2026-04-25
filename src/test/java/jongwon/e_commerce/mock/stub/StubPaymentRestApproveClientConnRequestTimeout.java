package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.gateway.toss.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.gateway.PaymentApproveClient;
import jongwon.e_commerce.payment.gateway.toss.dto.TossPaymentApproveResponse;
import org.apache.hc.core5.http.ConnectionRequestTimeoutException;
import org.springframework.web.client.ResourceAccessException;

public class StubPaymentRestApproveClientConnRequestTimeout implements PaymentApproveClient {

    @Override
    public TossPaymentApproveResponse callPayApprovalApi(PayApproveAttempt request) {
        ConnectionRequestTimeoutException connectionRequestTimeoutException = new ConnectionRequestTimeoutException();
        ResourceAccessException resourceAccessException = new ResourceAccessException("I/O Error", connectionRequestTimeoutException);
        throw resourceAccessException;
    }
}
