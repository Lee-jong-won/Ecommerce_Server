package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.toss.dto.TossPaymentApproveRequest;
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
    private final TossPaymentNetworkExceptionTranslator tossPaymentNetworkExceptionTranslator;

    @Override
    public TossPaymentApproveResponse callPayApprovalApi(TossPaymentApproveRequest request, String idempotencyKey) {
        TossPaymentApproveResponse response;
        try{
            response = restClient.post()
                    .uri("/payments/confirm")
                    .header("Idempotency-Key", idempotencyKey)
                    .body(request)
                    .retrieve()
                    .body(TossPaymentApproveResponse.class);
        } catch(ResourceAccessException e){
            throw tossPaymentNetworkExceptionTranslator.translateNetworkException(e);
        }
        return response;
    }

    @Override
    public TossPaymentCancelResponse callPayCancelApi(String paymentKey, String idempotencyKey, String cancelReason) {
        TossPaymentCancelResponse response;
        try {
            response = restClient.post()
                    .uri("/{paymentKey}/cancel", paymentKey)
                    .header("Idempotency-Key", idempotencyKey)
                    .body(Map.of("cancelReason", cancelReason))
                    .retrieve()
                    .body(TossPaymentCancelResponse.class);   // Void면 이게 더 깔끔
        } catch(ResourceAccessException e){
            throw tossPaymentNetworkExceptionTranslator.translateNetworkException(e);
        }
        return response;
    }
}
