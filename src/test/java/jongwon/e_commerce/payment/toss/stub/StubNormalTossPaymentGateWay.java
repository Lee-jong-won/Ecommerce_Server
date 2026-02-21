package jongwon.e_commerce.payment.toss.stub;

import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.dto.TossPaymentCancelRequest;
import jongwon.e_commerce.payment.toss.TossPaymentGateWay;
import jongwon.e_commerce.payment.toss.TossPaymentHttpClient;
import org.springframework.retry.support.RetryTemplate;

import java.time.OffsetDateTime;

public class StubNormalTossPaymentGateWay extends TossPaymentGateWay {
    public StubNormalTossPaymentGateWay(TossPaymentHttpClient tossPaymentHttpClient, RetryTemplate retryTemplate) {
        super(tossPaymentHttpClient, retryTemplate);
    }

    @Override
    public TossPaymentApproveResponse payApprove(TossPaymentApproveRequest request, String idempotencyKey) {
        return new TossPaymentApproveResponse("카드",
                OffsetDateTime.parse("2024-02-13T03:18:14Z"), "DONE");
    }

    @Override
    public void payCancel(TossPaymentCancelRequest request) {
        return;
    }
}
