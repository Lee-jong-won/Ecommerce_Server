package jongwon.e_commerce.payment.infrastructure.gateway.toss;

import jongwon.e_commerce.payment.domain.approve.outcome.PayApproveOutcome;
import jongwon.e_commerce.payment.domain.approve.outcome.success.PayApproveSuccess;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.infrastructure.gateway.PaymentClient;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.infrastructure.gateway.dto.PayResultResponseMapper;
import jongwon.e_commerce.payment.infrastructure.gateway.toss.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.infrastructure.gateway.toss.dto.TossPaymentApproveResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.RetryOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component("tossPaymentClient")
public class TossPayClient implements PaymentClient {

    private final RestClient restClient;
    private final RetryOperations payApproveRetryOperation;

    public TossPayClient(@Qualifier("tossRestClient")RestClient restClient,
                         @Qualifier("tossRetryTemplate")RetryOperations retryOperations){
        this.restClient = restClient;
        this.payApproveRetryOperation = retryOperations;
    }

    @Override
    public PayApproveOutcome callPayApprovalApi(PayApproveAttempt request) {

        TossPaymentApproveRequest tossPaymentApproveRequest = TossPaymentApproveRequest.from(request);

        TossPaymentApproveResponse tossPaymentApproveResponse = payApproveRetryOperation.execute(context -> {
            return restClient.post()
                    .uri("/payments/confirm")
                    .header("Idempotency-Key", generateIdempotencyKey(request.getOrderId()))
                    .body(tossPaymentApproveRequest)
                    .retrieve()
                    .body(TossPaymentApproveResponse.class);
        });

        return new PayApproveSuccess(PayResultResponseMapper.from(tossPaymentApproveResponse));
    }

    private String generateIdempotencyKey(String baseKey){
        return baseKey;
    }
}
