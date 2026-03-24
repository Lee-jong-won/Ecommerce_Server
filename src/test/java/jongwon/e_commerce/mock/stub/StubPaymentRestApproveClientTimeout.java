package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.PaymentApproveClient;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;

public class StubPaymentRestApproveClientTimeout implements PaymentApproveClient {

    @Override
    public TossPaymentApproveResponse callPayApprovalApi(PayApproveAttempt request, String idempotencyKey) {
        throw new ResourceAccessException("타임아웃 발생", new SocketTimeoutException());
    }


}
