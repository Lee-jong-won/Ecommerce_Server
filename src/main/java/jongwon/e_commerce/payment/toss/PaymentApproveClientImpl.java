package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.RetryOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Primary
@RequiredArgsConstructor
public class PaymentApproveClientImpl implements PaymentApproveClient {

    @Qualifier("tossRestClient")private final RestClient restClient;
    @Qualifier("tossRetryTemplate")private final RetryOperations payApproveRetryOperation;
    @Override
    public TossPaymentApproveResponse callPayApprovalApi(PayApproveAttempt request, String idempotencyKey) {
        return payApproveRetryOperation.execute(context -> {
            return restClient.post()
                    .uri("/payments/confirm")
                    .header("Idempotency-Key", idempotencyKey)
                    .body(request)
                    .retrieve()
                    .body(TossPaymentApproveResponse.class);
        });
    }
}
