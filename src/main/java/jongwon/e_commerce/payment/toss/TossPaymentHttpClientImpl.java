package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@Primary
@RequiredArgsConstructor
public class TossPaymentHttpClientImpl implements TossPaymentHttpClient {

    @Qualifier("tossRestClient")private final RestClient restClient;
    @Override
    public TossPaymentApproveResponse callPayApprovalApi(TossPaymentApproveRequest request, String idempotencyKey) {
        return restClient.post()
                .uri("/payments/confirm")
                .header("Idempotency-Key", idempotencyKey)
                .body(request)
                .retrieve()
                .body(TossPaymentApproveResponse.class);
    }

    @Override
    public void callPayCancelApi(String paymentKey, String idempotencyKey, String cancelReason) {
        restClient.post()
                .uri("/{paymentKey}/cancel", paymentKey)
                .header("Idempotency-Key", idempotencyKey)
                .body(Map.of("cancelReason", cancelReason))
                .retrieve()
                .toBodilessEntity();   // Void면 이게 더 깔끔
    }
}
