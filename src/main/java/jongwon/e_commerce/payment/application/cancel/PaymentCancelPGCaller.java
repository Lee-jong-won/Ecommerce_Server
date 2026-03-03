package jongwon.e_commerce.payment.application.cancel;

import jongwon.e_commerce.payment.toss.dto.PayCancelAttempt;
import jongwon.e_commerce.payment.toss.dto.TossPaymentCancelResponse;
import jongwon.e_commerce.payment.toss.TossPaymentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PaymentCancelPGCaller {

    private final TossPaymentClient tossPaymentClient;
    private final @Qualifier("tossRetryTemplate") RetryTemplate retryTemplate;

    public TossPaymentCancelResponse callPayCancelApi(PayCancelAttempt request) {
        return retryTemplate.execute(context -> {
            TossPaymentCancelResponse response = tossPaymentClient.callPayCancelApi(request.getPaymentKey(),
                    request.getCancelReason(), request.getIdempotencyKey());
            return response;
        });
    }
}
