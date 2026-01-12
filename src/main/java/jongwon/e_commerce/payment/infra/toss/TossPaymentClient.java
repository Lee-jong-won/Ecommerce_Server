package jongwon.e_commerce.payment.infra.toss;

import org.apache.hc.client5.http.classic.HttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Component
public class TossPaymentClient {
    private final RestClient restClient;

    public TossPaymentClient(TossPaymentProperties properties, @Qualifier("paymentHttpClient")HttpClient httpClient){
        this.restClient = RestClient.builder()
                .baseUrl(properties.getApiUrl())
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        buildBasicAuthHeader(properties.getSecretKey(), properties.getBasicAuthPrefix())
                )
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }

    private String buildBasicAuthHeader(String secretKey, String basicAuthPrefix){
        return basicAuthPrefix + " " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }

    public TossPaymentApproveResponse approvePayment(String paymentKey,
                                                     String orderId,
                                                     Long amount){
        return restClient.post()
                .uri("/confirm")
                .body(Map.of(
                        "paymentKey", paymentKey,
                        "orderId", orderId,
                        "amount", amount
                ))
                .retrieve()
                .body(TossPaymentApproveResponse.class);
    }

}
