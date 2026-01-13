package jongwon.e_commerce.payment.infra.toss;

import jongwon.e_commerce.external.http.client.HttpClientFactory;
import jongwon.e_commerce.external.http.policy.HttpClientPolicy;
import jongwon.e_commerce.external.http.policy.RetryPolicy;
import jongwon.e_commerce.external.http.policy.TimeoutPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
public class TossPaymentClient {
    private final RestClient restClient;
    public TossPaymentClient(
            HttpClientFactory factory,
            TossPaymentProperties properties
    ) {
        this.restClient = createRestClient(factory, properties);
    }

    private RestClient createRestClient(
            HttpClientFactory factory,
            TossPaymentProperties properties
    ) {
        HttpClientPolicy policy = HttpClientPolicy.builder()
                .timeoutPolicy(
                        TimeoutPolicy.builder()
                                .responseTimeoutSeconds(30)
                                .build()
                )
                .retryPolicy(
                        RetryPolicy.builder()
                                .disableAutomaticRetries(true)
                                .build()
                )
                .build();

        return RestClient.builder()
                .baseUrl(properties.getApiUrl())
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        buildBasicAuthHeader(
                                properties.getSecretKey(),
                                properties.getBasicAuthPrefix()
                        )
                )
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .requestFactory(
                        new HttpComponentsClientHttpRequestFactory(
                                factory.create(policy)
                        )
                )
                .build();
    }

    private String buildBasicAuthHeader(String secretKey, String basicAuthPrefix){
        return basicAuthPrefix + " " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }

    public TossPaymentApproveResponse approvePayment(String paymentKey,
                                                     String orderId,
                                                     Long amount){
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
         log.warn("[TOSS_PAYMENT_ERROR] orderId={}, paymentKey={}",
                 orderId,
                 paymentKey,
                 e);
         throw e;
     } catch ( ResourceAccessException e){
         log.error("[TOSS_PAYMENT_NE] orderId={}, paymentKey={}",
                 orderId,
                 paymentKey,
                 e);
         throw e;
     }
    }
}
