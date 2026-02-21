package jongwon.e_commerce.payment.toss.stub;

import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.dto.TossPaymentCancelRequest;
import jongwon.e_commerce.payment.exception.external.TossPaymentClientException;
import jongwon.e_commerce.payment.toss.TossPaymentGateWay;
import jongwon.e_commerce.payment.toss.TossPaymentHttpClient;
import org.springframework.retry.support.RetryTemplate;

public class StubClientExceptionTossPaymentGateWay extends TossPaymentGateWay {
    public StubClientExceptionTossPaymentGateWay(TossPaymentHttpClient tossPaymentHttpClient, RetryTemplate retryTemplate) {
        super(tossPaymentHttpClient, retryTemplate);
    }

    @Override
    public TossPaymentApproveResponse payApprove(TossPaymentApproveRequest request, String idempotencyKey) {
        throw new TossPaymentClientException("클라이언트 예외");
    }

    @Override
    public void payCancel(TossPaymentCancelRequest request) {
        throw new TossPaymentClientException("클라이언트 예외");
    }
}
