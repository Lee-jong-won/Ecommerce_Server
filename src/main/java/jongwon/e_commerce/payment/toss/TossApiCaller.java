package jongwon.e_commerce.payment.toss;

import jongwon.e_commerce.payment.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.dto.TossPaymentApproveResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@Primary
@Slf4j
@RequiredArgsConstructor
public class TossApiCaller{

    private final @Qualifier("tossRestClient")RestClient restClient;
    private final TossNetworkExceptionTranslator translator;

    public TossPaymentApproveResponse approve(
            TossPaymentApproveRequest request,
            String idempotencyKey
    ) {
        TossPaymentApproveResponse response;
        try{
            response = restClient.post()
                    .uri("/payments/confirm")
                    .header("Idempotency-Key", idempotencyKey)
                    .body(request)
                    .retrieve()
                    .body(TossPaymentApproveResponse.class);

        } catch( ResourceAccessException e){
            throw translator.translateNetworkException(e);
        }

        return response;
    }

    public void cancel(
            String paymentKey,
            String cancelReason,
            String idempotencyKey
    ) {
        try {
            restClient.post()
                    .uri("/{paymentKey}/cancel", paymentKey)
                    .header("Idempotency-Key", idempotencyKey)
                    .body(Map.of("cancelReason", cancelReason))
                    .retrieve()
                    .toBodilessEntity();   // Void면 이게 더 깔끔
        } catch( ResourceAccessException e){
            throw translator.translateNetworkException(e);
        }
    }
}
