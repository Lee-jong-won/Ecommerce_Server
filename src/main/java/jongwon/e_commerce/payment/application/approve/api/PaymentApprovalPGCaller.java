package jongwon.e_commerce.payment.application.approve.api;

import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.toss.TossPaymentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PaymentApprovalPGCaller {

    private final TossPaymentClient tossPaymentClient;
    private final @Qualifier("tossRetryTemplate") RetryTemplate retryTemplate;

    public TossPaymentApproveResponse callPayApproveApi(TossPaymentApproveRequest request, String idempotencyKey){
        return retryTemplate.execute(context -> {
            TossPaymentApproveResponse response = tossPaymentClient.callPayApprovalApi(request, idempotencyKey);
            return response;
        });
    }
}
