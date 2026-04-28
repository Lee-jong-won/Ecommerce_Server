package jongwon.e_commerce.payment.gateway.toss;

import jongwon.e_commerce.payment.gateway.dto.result.PayResult;
import jongwon.e_commerce.payment.gateway.PaymentClient;
import jongwon.e_commerce.payment.gateway.dto.PayApproveAttempt;
import jongwon.e_commerce.payment.gateway.dto.PayResultResponseMapper;
import jongwon.e_commerce.payment.gateway.toss.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.gateway.toss.dto.TossPaymentApproveResponse;
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
    public PayResult callPayApprovalApi(PayApproveAttempt request) {

        TossPaymentApproveRequest tossPaymentApproveRequest = TossPaymentApproveRequest.
                builder().
                paymentKey(request.getPaymentKey()).
                orderId(request.getOrderId()).
                amount(request.getAmount()).
                build();

        TossPaymentApproveResponse tossPaymentApproveResponse = payApproveRetryOperation.execute(context -> {
            return restClient.post()
                    .uri("/payments/confirm")
                    .header("Idempotency-Key", generateIdempotencyKey(request.getOrderId()))
                    .body(tossPaymentApproveRequest)
                    .retrieve()
                    .body(TossPaymentApproveResponse.class);
        });

        return PayResultResponseMapper.from(tossPaymentApproveResponse);
    }

    private String generateIdempotencyKey(String baseKey){
        return baseKey;
    }
}
