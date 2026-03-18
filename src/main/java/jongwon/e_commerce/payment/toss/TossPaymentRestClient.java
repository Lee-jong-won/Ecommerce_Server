package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.domain.approve.PayApproveAttempt;
import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.toss.dto.TossPaymentCancelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@Primary
@RequiredArgsConstructor
public class TossPaymentRestClient implements TossPaymentClient {

    @Qualifier("tossRestClient")private final RestClient restClient;

    @Override
    public TossPaymentApproveResponse callPayApprovalApi(PayApproveAttempt request, String idempotencyKey) {
        return restClient.post()
                .uri("/payments/confirm")
                .header("Idempotency-Key", idempotencyKey)
                .body(request)
                .retrieve()
                .body(TossPaymentApproveResponse.class);
    }

    @Override
    public TossPaymentCancelResponse callPayCancelApi(String paymentKey, String idempotencyKey, String cancelReason) {
        return restClient.post()
                .uri("/{paymentKey}/cancel", paymentKey)
                .header("Idempotency-Key", idempotencyKey)
                .body(Map.of("cancelReason", cancelReason))
                .retrieve()
                .body(TossPaymentCancelResponse.class);
    }
}
