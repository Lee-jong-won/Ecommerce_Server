package jongwon.e_commerce.mock.stub;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.TossPaymentClient;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.toss.dto.TossPaymentCancelResponse;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;

public class StubTossPaymentRestClientTimeout implements TossPaymentClient {

    @Override
    public TossPaymentApproveResponse callPayApprovalApi(PayApproveAttempt request, String idempotencyKey) {
        throw new ResourceAccessException("타임아웃 발생", new SocketTimeoutException());
    }

    @Override
    public TossPaymentCancelResponse callPayCancelApi(String paymentKey, String cancelReason, String idempotencyKey) {
        return null;
    }
}
