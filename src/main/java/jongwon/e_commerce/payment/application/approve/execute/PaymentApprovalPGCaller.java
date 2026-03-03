package jongwon.e_commerce.payment.application.approve.execute;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
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

    public TossPaymentApproveResponse callPayApproveApi(PayApproveAttempt request, String idempotencyKey){
        return retryTemplate.execute(context ->
                        tossPaymentClient.callPayApprovalApi(request, idempotencyKey)
                );
    }
}
