package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.gateway.PaymentClient;
import org.apache.hc.core5.http.ConnectionRequestTimeoutException;
import org.springframework.web.client.ResourceAccessException;

public class StubPaymentRestApproveClientConnRequestTimeout implements PaymentClient {

    @Override
    public PayResult callPayApprovalApi(PayApproveAttempt request) {
        ConnectionRequestTimeoutException connectionRequestTimeoutException = new ConnectionRequestTimeoutException();
        ResourceAccessException resourceAccessException = new ResourceAccessException("I/O Error", connectionRequestTimeoutException);
        throw resourceAccessException;
    }
}
