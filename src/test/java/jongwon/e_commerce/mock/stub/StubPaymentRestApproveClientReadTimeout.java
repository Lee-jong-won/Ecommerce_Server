package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.gateway.PaymentClient;
import jongwon.e_commerce.payment.gateway.toss.dto.TossPaymentApproveResponse;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;

public class StubPaymentRestApproveClientReadTimeout implements PaymentClient {

    @Override
    public TossPaymentApproveResponse callPayApprovalApi(PayApproveAttempt request) {
        SocketTimeoutException cause = new SocketTimeoutException("Read timed out");
        ResourceAccessException ex = new ResourceAccessException("I/O error", cause);
        throw ex;
    }

}
