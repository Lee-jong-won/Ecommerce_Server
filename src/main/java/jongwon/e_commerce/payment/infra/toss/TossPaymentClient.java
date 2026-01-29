package jongwon.e_commerce.payment.infra.toss;

import jongwon.e_commerce.payment.exception.TossPaymentErrorMapper;
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
        try {
            return restClient.post()
                    .uri(uri)
                    .body(body)
                    .retrieve()
                    .body(responseType);

        } catch (RestClientResponseException e) {
            throw handleApiError(e);

        } catch (ResourceAccessException e) {
            throw handleNetworkError(e);
        }
    }

    private <T> T execute(
            String uri,
            String paymentKey,
            Object body,
            Class<T> responseType
    ) {
        try {
            return restClient.post()
                    .uri(uri, paymentKey)
                    .body(body)
                    .retrieve()
                    .body(responseType);

        } catch (RestClientResponseException e) {
            throw handleApiError(e);

        } catch (ResourceAccessException e) {
            throw handleNetworkError(e);
        }
    }

    private <T> T execute(
            String uri,
            Object body,
            Class<T> responseType,
            Consumer<HttpHeaders> headerConsumer
    ) {
        try {
            return restClient.post()
                    .uri(uri)
                    .headers(headerConsumer)
                    .body(body)
                    .retrieve()
                    .body(responseType);

        } catch (RestClientResponseException e) {
            throw handleApiError(e);

        } catch (ResourceAccessException e) {
            throw handleNetworkError(e);
        }
    }

    private <T> T execute(
            String uri,
            String paymentKey,
            Object body,
            Class<T> responseType,
            Consumer<HttpHeaders> headerConsumer
    ) {
        try {
            return restClient.post()
                    .uri(uri, paymentKey)
                    .headers(headerConsumer)
                    .body(body)
                    .retrieve()
                    .body(responseType);

        } catch (RestClientResponseException e) {
            throw handleApiError(e);

        } catch (ResourceAccessException e) {
            throw handleNetworkError(e);
        }
    }

    /* ======================
     에러 처리
     ====================== */
    private TossPaymentException handleApiError(RestClientResponseException e) {
        TossErrorResponse error = parseError(e);
        TossPaymentException ex = tossPaymentErrorMapper.map(error.getCode());

        if (ex instanceof TossPaymentSystemException) {
            log.error(
                    "[TOSS_API_ERROR] code={}, message={}",
                    error.getCode(),
                    error.getMessage()
            );
        }
        return ex;
    }

    private TossPaymentException handleNetworkError(ResourceAccessException e) {
        if (e.getCause() instanceof SocketTimeoutException) {
            return new TossApiTimeoutException(PaymentErrorCode.TOSS_API_TIMEOUT_ERROR);
        }

        log.error("[TOSS_API_NETWORK_ERROR]", e);
        return new TossApiNetworkException(PaymentErrorCode.TOSS_API_NETWORK_ERROR);
    }

    private TossErrorResponse parseError(RestClientResponseException e) {
        try {
            return objectMapper.readValue(
                    e.getResponseBodyAsString(),
                    TossErrorResponse.class
            );
        } catch (JacksonException parseException) {
            log.error(
                    "[TOSS_ERROR_PARSE_FAIL] status={}, body={}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    parseException
            );
            return new TossErrorResponse(
                    "UNKNOWN_ERROR",
                    "결제 처리 중 알 수 없는 오류가 발생했습니다."
            );
        }
    }
}
