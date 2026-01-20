package jongwon.e_commerce.payment.infra.toss;

import jongwon.e_commerce.common.exception.ErrorCode;
import jongwon.e_commerce.payment.exception.TossPaymentErrorMapper;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossApiNetworkException;
import jongwon.e_commerce.payment.exception.external.TossPaymentRetryableException.TossApiTimeoutException;
import jongwon.e_commerce.payment.presentation.dto.TossErrorResponse;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.net.SocketTimeoutException;
import java.util.Map;

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

    public TossPaymentApproveResponse approvePayment(String paymentKey, String orderId, Long amount){
        try {
            return restClient.post()
                 .uri("/confirm")
                 .body(Map.of(
                         "paymentKey", paymentKey,
                         "orderId", orderId,
                         "amount", amount
                 ))
                 .retrieve()
                 .body(TossPaymentApproveResponse.class);
        } catch ( RestClientResponseException e){

            TossErrorResponse error = parseError(e);
            if ("INVALID_API_KEY".equals(error.getCode())
                    || "UNAUTHORIZED_KEY".equals(error.getCode())) {
                log.error("[TOSS_PAYMENT_ERROR] orderId={}, paymentKey={}, code={}, message={}",
                        orderId,
                        paymentKey,
                        error.getCode(),
                        error.getMessage()
                );
            }

            throw tossPaymentErrorMapper.map(error.getCode());
        } catch ( ResourceAccessException e){
            if(e.getCause() instanceof SocketTimeoutException)
                throw new TossApiTimeoutException(ErrorCode.TOSS_API_TIMEOUT_ERROR);
            else{
                log.error("[TOSS_PAYMENT_NETWORK_ERROR] orderId={}, paymentKey={}",
                        orderId,
                        paymentKey,
                        e);
                throw new TossApiNetworkException(ErrorCode.TOSS_API_NETWORK_ERROR);
            }
        }
    }

    private TossErrorResponse parseError(RestClientResponseException e) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(
                    e.getResponseBodyAsString(),
                    TossErrorResponse.class
            );

        } catch (JacksonException parseException) {
            log.error("[TOSS_ERROR_PARSE_FAIL] status={}, body={}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString(),
                    parseException
            );

            // 파싱 실패 시 fallback
            return new TossErrorResponse(
                    "UNKNOWN_ERROR",
                    "결제 처리 중 알 수 없는 오류가 발생했습니다."
            );
        }
    }
}
