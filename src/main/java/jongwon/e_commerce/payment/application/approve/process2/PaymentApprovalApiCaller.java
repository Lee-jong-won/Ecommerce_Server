package jongwon.e_commerce.payment.application.approve.process2;

import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.toss.TossPaymentHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PaymentApprovalApiCaller {

    private final TossPaymentHttpClient tossPaymentHttpClient;
    private final @Qualifier("tossRetryTemplate") RetryTemplate retryTemplate;

    public TossPaymentApproveResponse callPayApproveApi(TossPaymentApproveRequest request, String idempotencyKey){
        return retryTemplate.execute(context -> {
            TossPaymentApproveResponse response = tossPaymentHttpClient.callPayApprovalApi(request, idempotencyKey);
            return response;
        });
    }
}
