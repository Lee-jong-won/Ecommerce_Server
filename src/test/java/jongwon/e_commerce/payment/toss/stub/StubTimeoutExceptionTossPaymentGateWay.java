package jongwon.e_commerce.payment.toss.stub;

import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.dto.TossPaymentCancelRequest;
import jongwon.e_commerce.payment.exception.external.TossPaymentTimeoutException;
import jongwon.e_commerce.payment.toss.TossPaymentGateWay;
import jongwon.e_commerce.payment.toss.TossPaymentHttpClient;
import org.springframework.retry.support.RetryTemplate;

public class StubTimeoutExceptionTossPaymentGateWay extends TossPaymentGateWay {
    public StubTimeoutExceptionTossPaymentGateWay(TossPaymentHttpClient tossPaymentHttpClient, RetryTemplate retryTemplate) {
        super(tossPaymentHttpClient, retryTemplate);
    }

    @Override
    public TossPaymentApproveResponse payApprove(TossPaymentApproveRequest request, String idempotencyKey) {
        throw new TossPaymentTimeoutException("타임아웃 예외 발생!");
    }

    @Override
    public void payCancel(TossPaymentCancelRequest request) {
        throw new TossPaymentTimeoutException("타임아웃 예외 발생!");
    }
}
