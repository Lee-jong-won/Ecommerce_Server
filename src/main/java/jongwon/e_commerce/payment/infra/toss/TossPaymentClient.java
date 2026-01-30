package jongwon.e_commerce.payment.infra.toss;

import jongwon.e_commerce.payment.exception.external.PaymentErrorCode;
import jongwon.e_commerce.payment.exception.external.TossPaymentException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossApiNetworkException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossApiTimeoutException;
import jongwon.e_commerce.payment.exception.external.TossPaymentSystemException.TossPaymentSystemException;
import jongwon.e_commerce.payment.presentation.dto.TossErrorResponse;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveRequest;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveResponse;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentCancelRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Component
public class TossPaymentClient {
    private final RestClient restClient;
    private final TossPaymentErrorMapper tossPaymentErrorMapper;
    private final ObjectMapper objectMapper;

    public TossPaymentClient(
            @Qualifier("tossRestClient") RestClient restClient,
            ObjectMapper objectMapper,
            TossPaymentErrorMapper tossPaymentErrorMapper
    ) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.tossPaymentErrorMapper = tossPaymentErrorMapper;
    }


    /* ======================
       결제 승인
       ====================== */
    public TossPaymentApproveResponse approve(TossPaymentApproveRequest request) {
        return execute(
                "/payments/confirm",
                request,
                TossPaymentApproveResponse.class,
                headers -> headers.add(
                        "Idempotency-Key",
                        request.getIdempotencyKey()
                )
        );
    }

    /* ======================
       결제 취소
       ====================== */
    public void cancel(TossPaymentCancelRequest request) {
        execute(
                "/{paymentKey}/cancel",
                request.getPaymentKey(),
                Map.of("cancelReason", request.getCancelReason()),
                Void.class,
                headers -> headers.add(
                        "Idempotency-Key",
                        request.getIdempotencyKey())
        );
    }

    /* ======================
       공통 execute
       ====================== */
    private <T> T execute(
            String uri,
            Object body,
            Class<T> responseType
    ) {
        return restClient.post()
                .uri(uri)
                .body(body)
                .retrieve()
                .body(responseType);
    }

    private <T> T execute(
            String uri,
            String paymentKey,
            Object body,
            Class<T> responseType
    ) {
        return restClient.post()
                .uri(uri, paymentKey)
                .body(body)
                .retrieve()
                .body(responseType);
    }

    private <T> T execute(
            String uri,
            Object body,
            Class<T> responseType,
            Consumer<HttpHeaders> headerConsumer
    ) {
        return restClient.post()
                .uri(uri)
                .headers(headerConsumer)
                .body(body)
                .retrieve()
                .body(responseType);
    }

    private <T> T execute(
            String uri,
            String paymentKey,
            Object body,
            Class<T> responseType,
            Consumer<HttpHeaders> headerConsumer
    ) {
        return restClient.post()
                .uri(uri, paymentKey)
                .headers(headerConsumer)
                .body(body)
                .retrieve()
                .body(responseType);
    }
}
