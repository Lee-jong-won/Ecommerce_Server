package jongwon.e_commerce.payment.gateway.toss;

import jongwon.e_commerce.payment.domain.approve.outcome.success.PayResult;
import jongwon.e_commerce.payment.gateway.PaymentApproveClient;
import jongwon.e_commerce.payment.gateway.toss.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.gateway.toss.dto.TossPaymentApproveResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.RetryOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Primary
public class PaymentApproveClientImpl implements PaymentApproveClient {

    private final RestClient restClient;
    private final RetryOperations payApproveRetryOperation;

    public PaymentApproveClientImpl(@Qualifier("tossRestClient")RestClient restClient,
                                    @Qualifier("tossRetryTemplate")RetryOperations retryOperations){
        this.restClient = restClient;
        this.payApproveRetryOperation = retryOperations;
    }

    @Override
    public PayResult callPayApprovalApi(PayApproveAttempt request) {
        TossPaymentApproveResponse tossPaymentApproveResponse = payApproveRetryOperation.execute(context -> {
            return restClient.post()
                    .uri("/payments/confirm")
                    .header("Idempotency-Key", generateIdempotencyKey(request.getOrderId()))
                    .body(request)
                    .retrieve()
                    .body(TossPaymentApproveResponse.class);
        });
        return tossPaymentApproveResponse.toPayResult();
    }

    private String generateIdempotencyKey(String baseKey){
        return baseKey;
    }
}
