package jongwon.e_commerce.payment.infra.toss;

import jongwon.e_commerce.payment.exception.TossApiNetworkException;
import jongwon.e_commerce.payment.exception.TossPaymentApprovalClientFailException;
import jongwon.e_commerce.payment.exception.TossPaymentApprovalPGFailException;
import jongwon.e_commerce.payment.presentation.dto.TossPaymentApproveResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import java.util.Map;

@Slf4j
@Component
public class TossPaymentClient {
    private final RestClient restClient;

    public TossPaymentClient(
            @Qualifier("tossRestClient") RestClient restClient
    ) {
        this.restClient = restClient;
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
            if(e.getStatusCode() == HttpStatus.BAD_REQUEST)
                throw new TossPaymentApprovalClientFailException();

            log.error("[TOSS_PAYMENT_API_ERROR] orderId={}, paymentKey={}, status={}",
                    orderId, paymentKey, e.getStatusCode(), e);
            throw new TossPaymentApprovalPGFailException();
        } catch ( ResourceAccessException e){
            log.error("[TOSS_PAYMENT_NETWORK_ERROR] orderId={}, paymentKey={}",
                 orderId,
                 paymentKey,
                 e);
            throw new TossApiNetworkException();
        }
    }
}
