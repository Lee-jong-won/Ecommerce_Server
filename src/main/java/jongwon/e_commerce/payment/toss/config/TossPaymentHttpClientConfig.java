package jongwon.e_commerce.payment.toss.config;

import jongwon.e_commerce.external.http.client.HttpClientFactory;
import jongwon.e_commerce.external.http.policy.ConnectionPolicy;
import jongwon.e_commerce.external.http.policy.RequestConfigPolicy;
import jongwon.e_commerce.external.http.policy.RetryPolicy;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Configuration
public class TossPaymentHttpClientConfig {
    @Bean(name = "tossHttpClient")
    public HttpClient tossHttpClient(){
        return HttpClientFactory.create(
                List.of(
                        RetryPolicy.builder().disableAutomaticRetries(true)
                                .build(),
                        ConnectionPolicy.builder().
                                socketTimeout(Timeout.ofSeconds(5)).
                                maxTotalConnections(200).
                                defaultMaxPerRoute(200).
                                build(),
                        RequestConfigPolicy.builder().
                                connectionRequestTimeout(Timeout.ofSeconds(5)).build()
                )
        );
    }

    @Bean(name = "tossRestClient")
    public RestClient createTossRestClient(
            @Qualifier("tossHttpClient") HttpClient tossHttpClient,
            TossPaymentProperties properties) {
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
                                tossHttpClient
                        )
                )
                .build();
    }


    private String buildBasicAuthHeader(String secretKey, String basicAuthPrefix){
        return basicAuthPrefix + " " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }
}
