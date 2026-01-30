package jongwon.e_commerce.payment.infra.toss;

import jongwon.e_commerce.external.http.client.HttpClientFactory;
import jongwon.e_commerce.external.http.policy.HttpClientPolicy;
import jongwon.e_commerce.external.http.policy.RetryPolicy;
import jongwon.e_commerce.external.http.policy.TimeoutPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class TossPaymentClientConfig {
    @Bean(name = "tossRestClient")
    public RestClient createRestClient(
            HttpClientFactory factory,
            TossPaymentProperties properties,
            TossPaymentClientErrorHandler tossPaymentClientErrorHandler
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
                .defaultStatusHandler(HttpStatusCode::isError, tossPaymentClientErrorHandler)
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
}
